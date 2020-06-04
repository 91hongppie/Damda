from django.shortcuts import render, get_object_or_404
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes, authentication_classes
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework_jwt.authentication import JSONWebTokenAuthentication
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import get_user_model
from .models import Photo, Album, Video, FamilyName
from accounts.models import Family, Device
from rest_framework import status
from .serializers import PhotoSerializer, AlbumSerializer, GetMemberSerializer, AlbumPutSerializer, VideoSerializer, FamilyNameSerializer, EditFaceSerializer
import face_recognition as fr
import skimage.io
from django.conf import settings
from json import JSONEncoder
import os
import shutil
import json
import numpy as np
from rest_framework import generics
from rest_framework.pagination import PageNumberPagination
from decouple import config
import requests
# Create your views here.

@api_view(['GET', 'POST'])
def photo(request, family_pk, album_pk):
    if request.method == 'GET':
        album = get_object_or_404(Album, pk=album_pk)
        photos = Photo.objects.filter(albums=album).order_by('-id')
        serializers = PhotoSerializer(photos, many=True)
        return Response(serializers.data)
    elif request.method == 'POST':
        photos = request.data['photos']
        photo_ids = list(map(int, photos.split(', ')))
        photos = Photo.objects.filter(id__in=photo_ids)
        album = get_object_or_404(Album, pk=album_pk)
        for photo in photos:
            photo.albums.remove(album)
            if len(photo.albums.all()) < 1:
                file_path = settings.MEDIA_ROOT + '/' + str(photo.pic_name[8:])
                if os.path.isfile(file_path):
                    os.remove(file_path)
                photo.delete()
        photo_list = Photo.objects.filter(albums=album)
        serializers = PhotoSerializer(photo_list, many=True)
        return Response(serializers.data)


@api_view(['GET', 'POST', ])
def albums(request, family_pk, user_pk):
    albums = Album.objects.filter(family=family_pk)
    serializers = AlbumSerializer(albums, many=True)
    datas = serializers.data
    for data in datas:
        if data['title'] == "기본 앨범":
            data['call'] = "기본 앨범"
        else:
            calls = get_object_or_404(FamilyName, user=user_pk, album=data['id'])
            data['call'] = calls.call
    return Response({"data": datas})


@api_view(['PUT', 'DELETE'])
def album(request, album_pk):
    if request.method == 'PUT':
        album = get_object_or_404(Album, pk=album_pk)
        image = request.data['image'].replace('"',"")
        serializers = AlbumPutSerializer(data={'id': album_pk, 'image': image}, instance=album)
        if serializers.is_valid():
            serializers.save()
            return Response(serializers.data)
        return Response(status=status.HTTP_400_BAD_REQUEST)
    elif request.method == 'DELETE':
        album = get_object_or_404(Album, pk=album_pk)
        photos = album.photos.all()
        for photo in photos:
            photo.albums.remove(album)
            if len(photo.albums.all()) < 1:
                file_path = settings.MEDIA_ROOT + '/' + str(photo.pic_name[8:])
                if os.path.isfile(file_path):
                    os.remove(file_path)
                photo.delete()
        serializers = AlbumPutSerializer(data={'id': album_pk, 'image': 'empty'}, instance=album)
        if serializers.is_valid():
            serializers.save()
        return Response({"data": "삭제완료"})
    return Response(status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST'])
def albumMember(request, family_pk, user_pk):
    if request.method == 'GET':
        albums = Album.objects.filter(family=family_pk, member=None).exclude(title="기본 앨범")
        if albums.count() == 0:
            return Response({"data": [{'title': '', 'image': '', 'id': None}]})
        serializers = AlbumSerializer(albums, many=True)
        datas = serializers.data
        for data in datas:
            calls = get_object_or_404(FamilyName, user=user_pk, album=data['id'])
            data['call'] = calls.call
        datas.append({'title': '', 'image': '', 'id': None})
        print(datas)
        return Response({"data": datas})
    return Response(status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST'])
def face(request, family_pk, user_pk):
    if request.method == 'GET':
        faces = Album.objects.filter(family=family_pk).exclude(title="기본 앨범")
        serializers = GetMemberSerializer(faces, many=True)
        datas = serializers.data
        for data in datas:
            calls = get_object_or_404(FamilyName, user=user_pk, album=data['id'])
            data['call'] = calls.call
        return Response({"data": datas})
    elif request.method == 'POST':
        image = fr.load_image_file(request.FILES['image'])
        faces_locations = fr.face_locations(image)
        if len(faces_locations) == 0:
            return Response(status=202, data={'message': '얼굴을 찾을 수 없습니다.'})
        elif len(faces_locations) > 1:
            return Response(status=202, data={'message': '얼굴이 하나 이상입니다.'})
        top, right, bottom, left = faces_locations[0]
        face = image[top:bottom, left:right]
        title = request.data['album_name'].replace('"',"")
        face_encoding = fr.face_encodings(face)
        if len(face_encoding) == 0:
            return Response(status=202, data={'message': '얼굴을 찾을 수 없습니다.'})
        ROOT_DIR = os.path.abspath("./")
        os.makedirs(os.path.join(ROOT_DIR, 'uploads/faces'), exist_ok=True)
        try:
            with open(f'uploads/faces/family_{family_pk}.json') as family:
                data = json.load(family)
            data[f'{family_pk}_{title}'] = [face_encoding[0].tolist()]
        except:
            data = {}
            data[f'{family_pk}_{title}'] = [face_encoding[0].tolist()]
        with open(f'uploads/faces/family_{family_pk}.json', 'w', encoding='utf-8') as family:
            json.dump(data, family, cls=NumpyArrayEncoder, ensure_ascii=-False, indent=2)
        
        albumSerializer = AlbumSerializer(data={'family':family_pk, 'title':title, 'image': "empty"})
        if albumSerializer.is_valid():
            albumSerializer.save()
            os.makedirs(os.path.join(ROOT_DIR, 'uploads/albums/{}'.format(family_pk)), exist_ok=True)
            image_path = 'uploads/albums/{}/{}'.format(family_pk, request.FILES['image'])
            album = get_object_or_404(Album, pk=albumSerializer.data['id'])
            albumSerializer2 = AlbumSerializer(
                data={'family':family_pk, 'title':title, 'image': image_path}, instance=album)
            if albumSerializer2.is_valid():
                albumSerializer2.save()
                save_path = os.path.join(ROOT_DIR, image_path)
                skimage.io.imsave(save_path, image)
                if makeFamilyName(family_pk, request.data, albumSerializer.data['id'], title):
                    album.delete()
                    return Response(status=status.HTTP_400_BAD_REQUEST)
                make_image = Photo.objects.create(pic_name=image_path, title=request.FILES['image'])
                make_image.albums.add(album)
                response_data = albumSerializer2.data
                response_data['call'] = title
                return Response(response_data)
            else:
                album.delete()
    return Response(status=status.HTTP_400_BAD_REQUEST)

namedic={
    '남동생엄마' : '엄마', '여동생엄마': '엄마', '남동생아빠': '아빠', '여동생아빠': '아빠',
    '형엄마': '엄마', '누나엄마': '엄마', '형아빠': '아빠', '누나아빠': '아빠',
    '오빠엄마': '엄마', '오빠아빠': '아빠', '언니엄마': '엄마', '언니아빠': '아빠',
    '엄마남동생': '아들', '엄마여동생': '딸', '아빠남동생': '아들', '아빠여동생': '딸',
    '엄마형': '아들', '엄마누나': '딸', '아빠형': '아들', '아빠누나': '딸',
    '엄마오빠': '아들', '아빠오빠': '아들', '엄마언니': '딸', '아빠언니': '딸',
    '아빠엄마': '아내', '엄마아빠': '남편',
    '남동생누나': '누나', '남동생형': '형', '여동생누나': '언니', '여동생형': '오빠',
    '남동생언니': '누나', '남동생오빠': '형', '여동생언니': '언니', '여동생오빠': '오빠',
    '누나남동생': '남동생', '형남동생': '남동생', '누나여동생': '여동생', '형여동생': '여동생',
    '언니남동생': '남동생', '오빠남동생': '남동생', '언니여동생': '여동생', '오빠여동생': '여동생',

    '언니언니': '언니', '누나누나': '언니', '형형': '형', '오빠오빠': '형',
    '남동생남동생': '형', '여동생여동생': '언니',

    '누나형': '남동생', '형누나': '여동생',
    '언니오빠': '남동생', '오빠언니': '여동생'

}
def makeFamilyName(family_pk, data, album, title):
    if title == "나":
        familyNameSerializer = FamilyNameSerializer(data={'user': data['user_id'], 'owner': data['user_id'], 'album': album, 'call':title})
        face = get_object_or_404(Album, pk=album)
        serializer = EditFaceSerializer(data={'member': data['user_id']}, instance=face)
        if serializer.is_valid():
            serializer.save()
        else:
            return True
    else:
        familyNameSerializer = FamilyNameSerializer(data={'user': data['user_id'], 'album': album, 'call':title})
    if familyNameSerializer.is_valid():
        familyNameSerializer.save()
        users = get_user_model().objects.filter(family=family_pk).exclude(pk=data['user_id'])
        for user in users:
            member = get_object_or_404(FamilyName, user=data['user_id'], owner=user.id)
            try:
                familyNameSerializer2 = FamilyNameSerializer(data={'user': user.id, 'album': album, 'call': namedic[member.call + title]})
            except KeyError:
                familyNameSerializer2 = FamilyNameSerializer(data={'user': user.id, 'album': album, 'call': title})
            if familyNameSerializer2.is_valid():
                familyNameSerializer2.save()
    else:
        return True
            


@api_view(['GET', 'POST', ])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def all_photo(request, family_pk):
    if request.method == 'GET':
        albums = Album.objects.filter(family=family_pk)
        photos = Photo.objects.filter(albums__in=albums).order_by('-id')
        serializers = PhotoSerializer(photos, many=True)
        print(serializers.data)
        return Response(serializers.data)
    elif request.method == 'POST':
        photos = request.data['photos']
        photo_ids = list(map(int, photos.split(', ')))
        photos = Photo.objects.filter(id__in=photo_ids)
        for photo in photos:
            file_path = settings.MEDIA_ROOT + '/' + str(photo.pic_name[8:])
            if os.path.isfile(file_path):
                os.remove(file_path)
        photos.delete()
        albums = Album.objects.filter(family=family_pk)
        photos = Photo.objects.filter(albums__in=albums).order_by('-id')
        serializers = PhotoSerializer(photos, many=True)
        return Response(serializers.data)


@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def addphoto(request):
    User = get_user_model()
    item = request.FILES.get('uploadImages')

    # 유저가 있는지 확인 
    try:
        user_id = int(request.POST.get('user_id'))
        user = get_object_or_404(User, id=user_id)
    except:
        return Response(data='Who are you?', status=status.HTTP_404_NOT_FOUND)
    
    albums = Album.objects.filter(family_id=user.family_id)
    album = albums[0]

    ROOT_DIR = os.path.abspath("./")
    os.makedirs(os.path.join(ROOT_DIR, 'uploads/albums/{}'.format(user.family_id)), exist_ok=True)
    
    image = fr.load_image_file(item)

    image_path = 'uploads/albums/{}/{}'.format(user.family_id, item.name + '.jpg')
    if len(Photo.objects.filter(pic_name=image_path)):
        print('이미 있는 사진입니다')
        return Response(status=status.HTTP_200_OK)
    skimage.io.imsave(image_path, image)
    make_image = Photo.objects.create(pic_name=image_path, title=item.name)
    faces = fr.face_locations(image)
    if len(faces) != 0:
        count = 0
        for face in faces:
            top, right, bottom, left = face
            image_face = image[top:bottom, left:right]
            unknown_face = fr.face_encodings(image_face)
            if len(unknown_face) == 0:
                if not Photo.objects.filter(pic_name=image_path):
                    make_image.albums.add(album)
                break
            try:
                with open(f'uploads/faces/family_{album.family.id}.json', 'r', encoding='utf-8') as family:
                    data = json.load(family)
            except:
                if not Photo.objects.filter(pic_name=image_path):
                    make_image.albums.add(album)
                break
            for album_name, data in data.items():
                for dt in data:
                    dt = [np.asarray(dt)]
                    distance = fr.face_distance(dt, unknown_face[0])
                    if distance < 0.44:
                        info = album_name.split('_')
                        user_album = Album.objects.filter(family=info[0], title=info[1])[0]
                        make_image.albums.add(user_album)
                        count += 1
                        break
        if count == 0:
            if not Photo.objects.filter(pic_name=image_path):
                make_image.albums.add(album)
    else:
        make_image.albums.add(album)
    return Response(status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def uploadEnd(request):
    User = get_user_model()
    user_id = int(request.POST.get('user_id'))
    user = get_object_or_404(User, id=user_id)

    family = User.objects.filter(family_id=user.family_id).exclude(id=user.id)
    devices = Device.objects.filter(owner__in=family)
    url = 'https://fcm.googleapis.com/fcm/send'

    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'key={config("AUTHORIZATION_TOKEN")}'
    }
    
    if len(devices) > 0:
        if len(devices) > 1:
            device_list = []

            for device in devices:
                device_list.append(device.device_token)
            
            data = {
                "registration_ids": device_list,
                "notification": {
                    "title": "담다",
                    "body": "새로운 사진이 올라왔습니다.",
                    "android_channel_id": "NEW"
                }
            }
        elif len(devices) == 1:
            data = {
                "to": devices[0].device_token,
                "notification": {
                    "title": "담다",
                    "body": "새로운 사진이 올라왔습니다.",
                    "android_channel_id": "NEW"
                }
            }
        response = requests.post(url, data=json.dumps(data), headers=headers)
        result = response.status_code
    
    return Response(result, status=status.HTTP_200_OK)


@api_view(['GET', 'POST', ])
def video(request,family_pk):
    if request.method == 'GET':
        videos = Video.objects.filter(family=family_pk)
        serializers = VideoSerializer(videos, many=True)
        print(serializers.data)
        return Response({"data": serializers.data})
    elif request.method == 'POST':
        serializer = VideoSerializer(data={'file':request.FILES['image'],'family':family_pk,'title':request.data.get('title')[1:-1]})
        serializer.is_valid()
        print(serializer.errors)
        if serializer.is_valid():
            serializer.save()
            # data = {
            #     'uploadPath': uploaded_file.file.url
            # }
            return Response(serializer.data)
    return JsonResponse(data)

@api_view(['DELETE'])
def detail_video(request,family_pk,video_pk):
    if request.method == 'DELETE':
        video = get_object_or_404(Video,pk=video_pk)
        video.delete()
        return Response({'asdf':'asdf'})



class NumpyArrayEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return JSONEncoder.default(self, obj)

class PostPagination(PageNumberPagination):
    page_size = 12
    
class PostListAPIView(generics.ListAPIView):    
    def get_queryset(self):
        albums = Album.objects.filter(family=self.kwargs['family_pk'])
        photo_query = []
        for album in albums:
            photos = Photo.objects.filter(album=album.id)
            photo_query += photos
        return photo_query
    
    serializer_class = PhotoSerializer
    pagination_class = PostPagination

class ListAPIView(generics.ListAPIView):
    def get_queryset(self):
        photos = Photo.objects.filter(album=self.kwargs['album_pk'])
        serializers = PhotoSerializer(photos, many=True)
        return serializers.data
    serializer_class = PhotoSerializer
    pagination_class = PostPagination

@api_view(['GET', ])
def parentphotos(request, family_pk, user_pk):
    albums = Album.objects.filter(family=family_pk)
    photos = Photo.objects.filter(album__in=albums)
    return Response(len(photos))


@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def autoaddphoto(request):
    User = get_user_model()
    item = request.FILES.get('uploadImages')

    # 유저가 있는지 확인 
    try:
        user_id = int(request.POST.get('user_id'))
        user = get_object_or_404(User, id=user_id)
    except:
        return Response(data='Who are you?', status=status.HTTP_404_NOT_FOUND)
    
    albums = Album.objects.filter(family_id=user.family_id)
    album = albums[0]

    ROOT_DIR = os.path.abspath("./")
    os.makedirs(os.path.join(ROOT_DIR, 'uploads/albums/{}'.format(user.family_id)), exist_ok=True)
    
    image = fr.load_image_file(item)

    image_path = 'uploads/albums/{}/{}'.format(user.family_id, item.name + '.jpg')
    if len(Photo.objects.filter(pic_name=image_path)):
        print('이미 있는 사진입니다')
        return Response(status=status.HTTP_200_OK)
    skimage.io.imsave(image_path, image)
    make_image = Photo.objects.create(pic_name=image_path, title=item.name)
    faces = fr.face_locations(image)
    if len(faces) != 0:
        count = 0
        for face in faces:
            top, right, bottom, left = face
            image_face = image[top:bottom, left:right]
            unknown_face = fr.face_encodings(image_face)
            if len(unknown_face) == 0:
                if not Photo.objects.filter(pic_name=image_path):
                    make_image.albums.add(album)
                break
            try:
                with open(f'uploads/faces/family_{album.family.id}.json', 'r', encoding='utf-8') as family:
                    data = json.load(family)
            except:
                if not Photo.objects.filter(pic_name=image_path):
                    make_image.albums.add(album)
                break
            for album_name, data in data.items():
                info = album_name.split('_')
                owner = User.objects.filter(family=info[0], state=3)
                familyname = FamilyName.objects.filter(uset=user, owner=owner)
                if familyname.call != info[1]:
                    for dt in data:
                        dt = [np.asarray(dt)]
                        distance = fr.face_distance(dt, unknown_face[0])
                        if distance < 0.44:
                            user_album = Album.objects.filter(family=info[0], title=info[1])[0]
                            make_image.albums.add(user_album)
                            count += 1
                            break
    return Response(status=status.HTTP_200_OK)

@api_view(['POST', ])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def move(request):
    if request.method == 'POST':
        photos = request.data['photos']
        photo_ids = list(map(int, photos.split(', ')))
        photos = Photo.objects.filter(id__in=photo_ids)
        album = get_object_or_404(Album, pk=request.data['albumId'].replace('"',""))
        moveAlbum = get_object_or_404(Album, pk=request.data['moveAlbumId'].replace('"',""))
        for photo in photos:
            photo.albums.remove(album)
            photo.albums.add(moveAlbum)
    
        photo_list = Photo.objects.filter(albums=album)
        serializers = PhotoSerializer(photo_list, many=True)
        return Response(serializers.data)
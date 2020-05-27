from django.shortcuts import render, get_object_or_404
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated, AllowAny
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import get_user_model
from .models import Photo, Album, FaceImage
from accounts.models import Family
from rest_framework import status
from .serializers import PhotoSerializer, AlbumSerializer, FaceSerializer, AlbumPutSerializer, GetFaceSerializer
import face_recognition as fr
import skimage.io
from django.conf import settings
import os
import shutil
# Create your views here.

@api_view(['GET', 'POST'])
def photo(request, family_pk, album_pk):
    if request.method == 'GET':
        photos = Photo.objects.filter(album=album_pk)
        serializers = PhotoSerializer(photos, many=True)
        return Response(serializers.data)
    elif request.method == 'POST':
        photos = request.data['photos']
        photo_ids = list(map(int, photos.split(', ')))
        photos = Photo.objects.filter(id__in=photo_ids)
        album_id = photos[0].album
        photos.delete()
        photo_list = Photo.objects.filter(album=album_id)
        serializers = PhotoSerializer(photo_list, many=True)
        return Response(serializers.data)

@api_view(['POST', ])
def photo_delete(request):
    photos = request.data['photos']
    photo_id = int(photos)
    # print(photo_id)
    # print(type(photos))
    # photo_ids = list(map(int, photos))
    photos = Photo.objects.filter(id=photo_id)
    album_id = photos[0].album
    photo_list = Photo.objects.filter(album=album_id)
    photos.delete()
    serializers = PhotoSerializer(photo_list, many=True)
    return Response(serializers.data)


@api_view(['GET', 'POST', ])
def albums(request, family_pk):
    albums = Album.objects.filter(family=family_pk)
    serializers = AlbumSerializer(albums, many=True)
    return Response({"data": serializers.data})


@api_view(['PUT', 'DELETE'])
def album(request, album_pk):
    if request.method == 'PUT':
        album = get_object_or_404(Album, pk=album_pk)
        image = request.data['image'].replace('"',"")[1::]
        serializers = AlbumPutSerializer(data={'id': album_pk, 'image': image}, instance=album)
        if serializers.is_valid():
            serializers.save()
            return Response(serializers.data)
        return Response(status=status.HTTP_400_BAD_REQUEST)
    elif request.method == 'DELETE':
        album = get_object_or_404(Album, pk=album_pk)
        file_path = settings.MEDIA_ROOT + '/albums/' + str(album.family.id) + '/' + str(album_pk)
        if os.path.isdir(file_path):
            shutil.rmtree(file_path)
        album.delete()
        return Response({"data": "삭제완료"})
    return Response(status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET', 'POST'])
def face(request, family_pk):
    if request.method == 'GET':
        faces = FaceImage.objects.filter(family=family_pk)
        serializers = GetFaceSerializer(faces, many=True)
        return Response({"data": serializers.data})
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
        albumSerializer = AlbumSerializer(data={'family':family_pk, 'title':title, 'image': "empty"})
        if albumSerializer.is_valid():
            albumSerializer.save()

            ROOT_DIR = os.path.abspath("./")
            os.makedirs(os.path.join(ROOT_DIR, 'uploads/faces/{}'.format(family_pk)), exist_ok=True)
            image_path = 'uploads/faces/{}/{}_{}'.format(family_pk, albumSerializer.data['id'], request.FILES['image'])
            save_path = os.path.join(ROOT_DIR, image_path)
            skimage.io.imsave(save_path, face)

            os.makedirs(os.path.join(ROOT_DIR, 'uploads/albums/{}/{}'.format(family_pk, albumSerializer.data['id'])), exist_ok=True)
            image_path2 = 'uploads/albums/{}/{}/{}'.format(family_pk, albumSerializer.data['id'], request.FILES['image'])
            save_path2 = os.path.join(ROOT_DIR, image_path2)
            skimage.io.imsave(save_path2, image)

            album = get_object_or_404(Album, pk=albumSerializer.data['id'])
            albumSerializer2 = AlbumSerializer(
                data={'family':family_pk, 'title':title, 'image': image_path2}, instance=album)
            if albumSerializer2.is_valid():
                albumSerializer2.save()
            serializers = FaceSerializer(data={'album': albumSerializer.data['id'], 'family': family_pk, 'image': image_path, 'name': title})
            if serializers.is_valid():
                serializers.save()
                return Response(serializers.data)
    return Response(status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST', ])
def all_photo(request, family_pk):
    return_list = []
    if request.method == 'GET':
        albums = Album.objects.filter(family=family_pk)
        for album in albums:
            photos = Photo.objects.filter(album=album.id)
            serializers = PhotoSerializer(photos, many=True)
            return_list += serializers.data
        return Response(return_list)
    elif request.method == 'POST':
        photos = request.data['photos']
        photo_ids = list(map(int, photos.split(', ')))
        photos = Photo.objects.filter(id__in=photo_ids)
        photos.delete()
        albums = Album.objects.filter(family=family_pk)
        for album in albums:
            photos = Photo.objects.filter(album=album.id)
            serializers = PhotoSerializer(photos, many=True)
            return_list += serializers.data
        return Response(return_list)




@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def addphoto(request):
    User = get_user_model()
    image_list = request.FILES.getlist('uploadImages')
    user_id = None
    
    try:
        user_id = int(request.POST.get('user_id'))
    except:
        user_id = 1
        print('Who are you?')
    
    user = get_object_or_404(User, id=user_id)
    try:
        print(user.family_id)
        albums = Album.objects.filter(family_id=user.family_id)
        album = albums[0]
    except:
        album = Album.objects.get(id=1)
        print('Family is NOT FOUND')

    for item in image_list:
        if len(Photo.objects.filter(title=item.name, album=album)):
            print('이미 있는 사진입니다')
            continue
        image = Photo.objects.create(pic_name=item, title=item.name, album=album)
        

    return Response(status=status.HTTP_200_OK)
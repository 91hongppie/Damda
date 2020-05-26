from django.shortcuts import render, get_object_or_404
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .models import Photo, Album, FaceImage, Video
from rest_framework import status
from .forms import UploadFileForm
from .serializers import PhotoSerializer, AlbumSerializer, FaceSerializer, VideoSerializer
import face_recognition as fr
import skimage.io
import os
# Create your views here.

@api_view(['GET', ])
def photo(request, album_pk):
    photos = Photo.objects.filter(album=album_pk)
    serializers = PhotoSerializer(photos, many=True)
    return Response(serializers.data)

@api_view(['POST', ])
def photo_delete(request):
    photos = request.data['photos']
    photo_id = int(photos)
    print(photo_id)
    # print(type(photos))
    # photo_ids = list(map(int, photos))
    photos = Photo.objects.filter(id=photo_id)
    album_id = photos[0].album
    photo_list = Photo.objects.filter(album=album_id)
    photos.delete()
    serializers = PhotoSerializer(photo_list, many=True)
    return Response(serializers.data)


@api_view(['GET', 'POST', ])
def album(request, family_pk):
    albums = Album.objects.filter(family=family_pk)
    serializers = AlbumSerializer(albums, many=True)
    return Response({"data": serializers.data})

@api_view(['GET', 'POST'])
def face(request, family_pk):
    if request.method == 'GET':
        faces = FaceImage.objects.filter(family=family_pk)
        serializers = FaceSerializer(faces, many=True)
        return Response({"data": serializers.data})
    elif request.method == 'POST':
        image = fr.load_image_file(request.FILES['image'])
        faces_locations = fr.face_locations(image)
        if len(faces_locations) == 0:
            return Response(status=403, data={'error': '얼굴을 찾을 수 없습니다.'})
        elif len(faces_locations) > 1:
            return Response(status=403, data={'error': '얼굴이 하나 이상입니다.'})
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
def video(request,family_pk):
    if request.method == 'GET':
        videos = Video.objects.filter(family=family_pk)
        serializers = VideoSerializer(videos, many=True)
        return Response({"data": serializers.data})
    elif request.method == 'POST':
        serializer = VideoSerializer(data={'file':request.FILES,'family':family_pk,'title':request.data['title']})
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
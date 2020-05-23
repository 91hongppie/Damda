from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .models import Photo, Album
from rest_framework import status
from .serializers import PhotoSerializer, AlbumSerializer, FaceSerializer
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

@api_view(['POST'])
def save_face(request, family_pk):
    image = fr.load_image_file(request.FILES['image'])
    top, right, bottom, left = fr.face_locations(image)[0]
    face = image[top:bottom, left:right]
    albumSerializer = AlbumSerializer(data={'family':family_pk, 'title':request.data['album_name']})
    if albumSerializer.is_valid():
        albumSerializer.save()
        ROOT_DIR = os.path.abspath("./")
        os.makedirs(os.path.join(ROOT_DIR, 'uploads/faces/{}'.format(family_pk)), exist_ok=True)
        image_path = 'uploads/faces/{}/{}_{}'.format(family_pk, albumSerializer.data['id'], request.FILES['image'])
        save_path = os.path.join(ROOT_DIR, image_path)
        skimage.io.imsave(save_path, face)
        serializers = FaceSerializer(data={'album': albumSerializer.data['id'], 'image': image_path})
        if serializers.is_valid():
            serializers.save()
            return Response(serializers.data)
    return Response(status=status.HTTP_400_BAD_REQUEST)

from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .models import Photo, Album
from .serializers import PhotoSerializer, AlbumSerializer

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
def album(request):
    albums = Album.objects.all()
    serializers = AlbumSerializer(albums, many=True)
    return Response({"data": serializers.data})

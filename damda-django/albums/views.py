from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .models import Photo, Album
from .serializers import PhotoSerializer, AlbumSerializer

# Create your views here.

@api_view(['GET', 'POST', ])
def photo(request, album_pk):
    photos = Photo.objects.all()
    serializers = PhotoSerializer(photos, many=True)
    print(serializers.data)
    return Response(serializers.data)

@api_view(['GET', 'POST', ])
def album(request):
    albums = Album.objects.all()
    serializers = AlbumSerializer(albums, many=True)
    return Response({"data": serializers.data})
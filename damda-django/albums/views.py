from django.shortcuts import render
from rest_framework.response import Response
from .models import Photo
from .serializers import PhotoSerializer

# Create your views here.

def photo(request):
    photos = Photo.objects.filter(id=1)
    serializers = PhotoSerializer(photos[0])
    return Response(serializers.data)
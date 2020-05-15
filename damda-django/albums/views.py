from django.shortcuts import render
from rest_framework.response import Response
from .models import Photo
from .serializers import PhotoSerializer

# Create your views here.

def photo(request):
    photos = Photo.objects.all()
    serializers = PhotoSerializer(photos, many=True)
    return Response(serializers.data)
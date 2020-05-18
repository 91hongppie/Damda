from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.decorators import api_view
from .models import Photo
from .serializers import PhotoSerializer

# Create your views here.

@api_view(['GET', 'POST', ])
def photo(request):
    photos = Photo.objects.all()
    serializers = PhotoSerializer(photos, many=True)

    return Response(serializers.data)
from rest_framework import serializers
from .models import Photo, Album, FaceImage

class AlbumSerializer(serializers.ModelSerializer):
    class Meta:
        model = Album
        fields = ('id', 'title', 'family', 'image')

class PhotoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Photo
        fields = ('id', 'pic_name', 'title')
    
class FaceSerializer(serializers.ModelSerializer):
    class Meta:
        model = FaceImage
        fields = ('id', 'album', 'image', 'name', 'family', 'member')

class EditFaceSerializer(serializers.ModelSerializer):
    class Meta:
        model = FaceImage
        fields = ('id', 'member')

class AlbumPutSerializer(serializers.ModelSerializer):
    class Meta:
        model = Album
        fields = ('id', 'image')

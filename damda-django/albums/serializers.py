from rest_framework import serializers
from .models import Photo, Album

class AlbumSerializer(serializers.ModelSerializer):
    class Meta(Album):
        model = Album
        fields = ('id', 'title')

class PhotoSerializer(serializers.ModelSerializer):
    class Meta(Photo):
        model = Photo
        fields = ('id', 'pic_name', 'title')
    


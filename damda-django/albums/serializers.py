from rest_framework import serializers
from .models import Photo, Album, FaceImage

class AlbumSerializer(serializers.ModelSerializer):
    class Meta(Album):
        model = Album
        fields = ('id', 'title')

class PhotoSerializer(serializers.ModelSerializer):
    class Meta(Photo):
        model = Photo
        fields = ('id', 'pic_name', 'title')
    
class FaceSerializer(serializers.ModelSerializer):
    image = serializers.ImageField(use_url=True)
    class Meta:
        model = FaceImage
        fields = ('id', 'album_name', 'image')

from rest_framework import serializers
from .models import Photo


class PhotoSerializer(serializers.ModelSerializer):
    class Meta(Photo):
        model = Photo
        fields = ('id', 'pic_name', 'title')
    


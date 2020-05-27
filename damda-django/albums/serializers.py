from rest_framework import serializers
from .models import Photo, Album, FaceImage
from django.contrib.auth import get_user_model


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

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ('username',)

class GetFaceSerializer(serializers.ModelSerializer):
    member_account = UserSerializer(source="member")
    class Meta:
        model = FaceImage
        fields = ('id', 'album', 'image', 'name', 'family', 'member', 'member_account')

class EditFaceSerializer(serializers.ModelSerializer):
    class Meta:
        model = FaceImage
        fields = ('id', 'member')

class AlbumPutSerializer(serializers.ModelSerializer):
    class Meta:
        model = Album
        fields = ('id', 'image')

from rest_framework import serializers
from .models import Photo, Album, Video, FamilyName
from django.contrib.auth import get_user_model


class AlbumSerializer(serializers.ModelSerializer):
    class Meta:
        model = Album
        fields = ('id', 'title', 'family', 'image', 'member')

class PhotoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Photo
        fields = ('id', 'pic_name', 'title')

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ('username',)

class GetMemberSerializer(serializers.ModelSerializer):
    member_account = UserSerializer(source="member")
    class Meta:
        model = Album
        fields = ('id', 'image', 'title', 'family', 'member', 'member_account')

class EditFaceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Album
        fields = ('id', 'member')

    
class VideoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Video
        fields = ('id', 'file', 'family', 'title')

class AlbumPutSerializer(serializers.ModelSerializer):
    class Meta:
        model = Album
        fields = ('id', 'image')

class FamilyNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = FamilyName
        fields = ('id', 'user', 'owner', 'album', 'call')

class FamilyNameupdateSerializer(serializers.ModelSerializer):
    class Meta:
        model = FamilyName
        fields = ('user', 'owner', 'album')
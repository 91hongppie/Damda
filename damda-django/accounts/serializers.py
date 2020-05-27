from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import WaitUser, Family, Device

class UserCreatSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ['id', 'username', 'email', 'password', 'first_name', 'birth', 'is_lunar']

    def create(self, validated_data):
        user = get_user_model().objects.create_user(**self.validated_data)
        return user

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ('id', 'username', 'state', 'family', 'first_name', 'birth', 'is_lunar')

class JoinFamilySerializer(serializers.ModelSerializer):
    class Meta:
        model = WaitUser
        fields = ('id', 'main_member', 'wait_user')


class FamilySerializer(serializers.ModelSerializer):
    class Meta:
        model = Family
        fields = ('id', 'main_member')

class DetailFamilySerializer(serializers.ModelSerializer):
    members = UserSerializer(source="user_family", many=True)
    class Meta:
        model = Family
        fields = ('id', 'main_member', 'members')

class DeviceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Device
        fields = ('id', 'device_token')

class WaitUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = WaitUser
        fields = ('id','wait_user')


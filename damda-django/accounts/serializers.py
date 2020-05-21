from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import WaitUser, Family

from .models import User
from django.contrib.auth import get_user_model

class UserCreatSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ['id', 'username', 'email', 'password']

    def create(self, validated_data):
        user = get_user_model().objects.create_user(**self.validated_data)
        return user

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ('id', 'username', 'state', 'family')

class JoinFamilySerializer(serializers.ModelSerializer):
    class Meta:
        model = WaitUser
        fields = ('id', 'main_member', 'wait_user')


class FamilySerializer(serializers.ModelSerializer):
    class Meta:
        model = Family
        fields = ('id', 'main_member')

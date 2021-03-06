from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import WaitUser, Family, Score, Mission, Quiz


class UserChangeSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ['id', 'username', 'password']
    
    def update(self, instance, validated_data):
        user = instance
        data = validated_data
        user.username = data.get('username', user.username)
        if data.get('password') != user.password:
            user.set_password(data.get('password'))
        user.save()
        return user

class UserCreatSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ['id', 'username', 'email', 'password', 'first_name', 'birth', 'is_lunar','gender']

    def create(self, validated_data):
        user = get_user_model().objects.create_user(**self.validated_data)
        return user

    def update(self, instance, validated_data):
        user = instance
        data = validated_data
        user.username = data.get('username', user.username)
        user.save()
        return user


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ('id', 'username', 'state', 'family', 'first_name', 'birth', 'is_lunar', 'gender')

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

class WaitUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = WaitUser
        fields = ('id','wait_user')

class ScoreSerializer(serializers.ModelSerializer):
    class Meta:
        model = Score
        fields = ('id', 'user', 'score')
        
class MissionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Mission
        fields = ('id', 'user', 'title', 'status', 'point', 'prize', 'period')

class QuizSerializer(serializers.ModelSerializer):
    class Meta:
        model = Quiz
        fields = ('id', 'user', 'quiz', 'answer')
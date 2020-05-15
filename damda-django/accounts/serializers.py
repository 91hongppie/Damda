from rest_framework import serializers
from .models import User

class UserSerializers(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = ('id', 'username')
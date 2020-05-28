from django.db import models
from django.contrib.auth.models import AbstractUser
# from django.conf import settings

# Create your models here.
class Family(models.Model):
    main_member = models.IntegerField()

# state 0: 가입만 1: 기다리는중 2: 가입 3: main_member
class User(AbstractUser):
    username = models.EmailField(unique=True, null=False, max_length=254)
    state = models.IntegerField(default=0)
    family = models.ForeignKey(Family, related_name='user_family', on_delete=models.SET_NULL, blank=True, null=True)
    birth = models.DateField(null=True)
    is_lunar = models.BooleanField(default=False)

class WaitUser(models.Model):
    main_member = models.ForeignKey(User, on_delete=models.CASCADE)
    wait_user = models.EmailField(max_length=254)

class Device(models.Model):
    device_token = models.CharField(unique=True, max_length=250)
    owner = models.ForeignKey(User, related_name='user_device', on_delete=models.CASCADE)
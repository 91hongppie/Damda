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
    switch = models.BooleanField(default=True)


class Score(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    score = models.IntegerField(default=0)

# status 0: 미션 진행중, 1: 미션 완료
# period 0: 일일 미션, 1: 주간 미션, 2: 월간 미션
# prize 0: 보상 안 받음 1: 보상 받음
class Mission(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    title = models.CharField(max_length=150)
    status = models.IntegerField(default=0)
    point = models.IntegerField()
    prize = models.IntegerField(default=0)
    period = models.IntegerField()
from django.db import models
from django.contrib.auth.models import AbstractUser
# from django.conf import settings

# Create your models here.

class User(AbstractUser):
    username = models.EmailField(unique=True, null=False, max_length=254)


# class Addresses(models.Model):
#     name = models.CharField(max_length=10)
#     phone_number = models.CharField(max_length=13)
#     address = models.TextField()
#     created = models.DateTimeField(auto_now_add=True)
#     class Meta:
#         ordering = ['created']
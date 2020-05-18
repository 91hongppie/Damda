from django.db import models
from django.contrib.auth.models import AbstractUser
# from django.conf import settings

# Create your models here.
class Family(models.Model):
    main_member = models.EmailField(max_length=254)

class User(AbstractUser):
    username = models.EmailField(unique=True, null=False, max_length=254)
    is_main_member = models.BooleanField(default=False)
    family = models.ForeignKey(Family, related_name='user_family', on_delete=models.PROTECT, blank=True, null=True)

# class Addresses(models.Model):
#     name = models.CharField(max_length=10)
#     phone_number = models.CharField(max_length=13)
#     address = models.TextField()
#     created = models.DateTimeField(auto_now_add=True)
#     class Meta:
#         ordering = ['created']
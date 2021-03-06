from django.db import models
from imagekit.models import ProcessedImageField
from imagekit.processors import ResizeToFit
from accounts.serializers import Family
from django.contrib.auth import get_user_model
import uuid
from django.db import models
 
# Create your models here.
class Album(models.Model):
    title = models.CharField(max_length=50)
    family = models.ForeignKey(Family, on_delete=models.CASCADE, related_name="face_album")
    image = models.CharField(max_length=500)
    member = models.ForeignKey(get_user_model(), on_delete=models.CASCADE, related_name="family_member", null=True)
    updated_at = models.DateTimeField(auto_now_add=True)

class Photo(models.Model):
    pic_name = models.CharField(max_length=500)
    title = models.TextField()
    albums = models.ManyToManyField(Album, related_name="photos", blank=True)
    uploaded_at = models.DateTimeField(auto_now_add=True)
    
def get_file_name(instance, filename):
    ext = filename.split('.')[-1]
    return "%s.%s" % (uuid.uuid4(), ext)
 
class Video(models.Model):
    file = models.FileField(upload_to=get_file_name)
    family = models.ForeignKey(Family,on_delete=models.CASCADE,related_name="family_video")
    title = models.TextField()

class FamilyName(models.Model):
    user =  models.ForeignKey(get_user_model(), on_delete=models.CASCADE, related_name="me_family")
    owner = models.ForeignKey(get_user_model(), on_delete=models.CASCADE, related_name="owner_album", null=True)
    album = models.ForeignKey(Album, on_delete=models.CASCADE, related_name="album_familyName", null=True)
    call = models.CharField(max_length=50)

from django.db import models
from imagekit.models import ProcessedImageField
from imagekit.processors import ResizeToFit
from accounts.serializers import Family
from django.contrib.auth import get_user_model
import uuid
from django.db import models
 
# Create your models here.
class Album(models.Model):
    title = models.TextField()
    family = models.ForeignKey(Family, on_delete=models.CASCADE, related_name="face_album")
    image = models.CharField(max_length=500)


class Photo(models.Model):
    pic_name = ProcessedImageField(
        processors=[ResizeToFit(128, 128)],
        format='JPEG',
        options={'quality': 100},
        upload_to = 'albums/pic_names',
        blank=False,
    )
    title = models.TextField()
    album = models.ForeignKey(Album, on_delete=models.CASCADE, related_name="album_photo")

class FaceImage(models.Model):
    album = models.ForeignKey(Album, on_delete=models.CASCADE)
    image = models.CharField(max_length=500)
    name = models.TextField()
    family = models.ForeignKey(Family, on_delete=models.CASCADE, related_name="family_face")
    member = models.ForeignKey(get_user_model(), on_delete=models.CASCADE, related_name="family_member", null=True)
    

def get_file_name(instance, filename):
    ext = filename.split('.')[-1]
    return "%s.%s" % (uuid.uuid4(), ext)
 
class Video(models.Model):
    file = models.FileField(upload_to=get_file_name)
    family = models.ForeignKey(Family,on_delete=models.CASCADE,related_name=family_video)
    title = models.TextField()
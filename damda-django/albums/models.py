from django.db import models
from imagekit.models import ProcessedImageField
from imagekit.processors import ResizeToFit
from accounts.serializers import Family
from django.contrib.auth import get_user_model

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
    




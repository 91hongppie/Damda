from django.db import models
from imagekit.models import ProcessedImageField
from imagekit.processors import ResizeToFit
from accounts.serializers import Family

# Create your models here.
class Album(models.Model):
    title = models.TextField()
    family = models.ForeignKey(Family, on_delete=models.CASCADE)


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




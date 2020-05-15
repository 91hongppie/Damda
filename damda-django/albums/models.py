from django.db import models
from imagekit.models import ProcessedImageField
from imagekit.processors import ResizeToFit

# Create your models here.
class Photo(models.Model):
    pic_name = ProcessedImageField(
        processors=[ResizeToFit(128, 128)],
        format='JPEG',
        options={'quality': 100},
        upload_to = 'albums/pic_names',
        blank=False,
    )
    title = models.TextField()




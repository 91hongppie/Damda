# Generated by Django 3.0.6 on 2020-05-25 05:58

from django.db import migrations, models
import django.db.models.deletion
import imagekit.models.fields


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('accounts', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Album',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('title', models.TextField()),
                ('image', models.CharField(max_length=500)),
                ('family', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='face_album', to='accounts.Family')),
            ],
        ),
        migrations.CreateModel(
            name='Photo',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('pic_name', imagekit.models.fields.ProcessedImageField(upload_to='albums/pic_names')),
                ('title', models.TextField()),
                ('album', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='album_photo', to='albums.Album')),
            ],
        ),
        migrations.CreateModel(
            name='FaceImage',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('image', models.CharField(max_length=500)),
                ('name', models.TextField()),
                ('album', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='albums.Album')),
                ('family', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='family_face', to='albums.Album')),
            ],
        ),
    ]

# Generated by Django 3.0.6 on 2020-06-03 07:40

import albums.models
from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('accounts', '0001_initial'),
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Album',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('title', models.CharField(max_length=50)),
                ('image', models.CharField(max_length=500)),
                ('updated_at', models.DateTimeField(auto_now_add=True)),
                ('family', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='face_album', to='accounts.Family')),
                ('member', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='family_member', to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='Video',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('file', models.FileField(upload_to=albums.models.get_file_name)),
                ('title', models.TextField()),
                ('family', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='family_video', to='accounts.Family')),
            ],
        ),
        migrations.CreateModel(
            name='Photo',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('pic_name', models.CharField(max_length=500)),
                ('title', models.TextField()),
                ('uploaded_at', models.DateTimeField(auto_now_add=True)),
                ('album', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='album_photo', to='albums.Album')),
            ],
        ),
        migrations.CreateModel(
            name='FamilyName',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('call', models.CharField(max_length=50)),
                ('album', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='album_familyName', to='albums.Album')),
                ('owner', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, related_name='owner_album', to=settings.AUTH_USER_MODEL)),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='me_family', to=settings.AUTH_USER_MODEL)),
            ],
        ),
    ]

<template>
    <v-container fluid>
      <v-row dense>
        <v-col
          v-for="album in albums"
          :key="album.id"
          cols="12"
          lg="3"
          md="4"
          sm="6"
        >
          <v-card @click="moveAlbum(album.id)">
            <v-img
              v-if="album.image=='empty'"
              :src="require('../assets/no_image.png')"
              class="white--text align-end"
              gradient="to bottom, rgba(0,0,0,.05), rgba(0,0,0,.0)"
              height="200px"
            >
            </v-img>
            <v-img
              v-else
              :src="baseURL + album.image"
              class="white--text align-end"
              gradient="to bottom, rgba(0,0,0,.05), rgba(0,0,0,.0)"
              height="200px"
            >
            </v-img>
            <v-card-title>{{ album.call }}</v-card-title>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
</template>

<script>
  import http from '../http-common'
  import { mapGetters } from 'vuex'

  export default {
    name: 'Albums',

    data: () => ({
      baseURL : "",
      albums: []
    }),

    computed: {
    ...mapGetters([
      'options',
      'user',
      'family',
    ]),
    },

    mounted() {
      this.baseURL = this.$store.state.server
      if (!this.family) {
        this.$router.replace('/login')
      }
      else {
        http.get(`/api/albums/${this.family}/${this.user.id}/`, this.options)
          .then(response => {
              this.albums = response.data.data
              console.log(response.data)
          })
          .catch(error => {
            console.log(error)
          })
      }
    },
    
    methods: {
      moveAlbum(album_id) {
        this.$emit('album-id', album_id)
      }
    }

  }
</script>
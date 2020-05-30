<template>
    <div>
    <v-container class="overflow-hidden">
    <v-app-bar
      absolute
      color="#92B5D9"
      dark
      shrink-on-scroll
      prominent
      :src="require('../assets/mainpage.jpg')"
      fade-img-on-scroll
      scroll-target="#scrolling-techniques-5"
      scroll-threshold="500"
    >
      <template v-slot:img="{ props }">
        <v-img
          v-bind="props"
          gradient="to top right, rgba(55,236,186,.7), rgba(146,181,217,.1)"
        ></v-img>
      </template>

      <v-toolbar-title>
          <v-img
          :src="require('../assets/logo_main.png')"
          contain
          height="35"
          width="100"
        />
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-btn text class="my-1" @click="logout">로그아웃</v-btn>
    </v-app-bar>
    </v-container>
    <v-sheet
      id="scrolling-techniques-5"
      class="overflow-y-auto"
      max-height="600"
      style="margin-top: 100px"
    >
      <v-container>
        <v-banner>
            {{ username }}<br>{{ account }}
        </v-banner>
        <v-tabs
      background-color="white"
      color="#92B5D9"
      grow
    >
      <v-tab @click="setAlbum">앨범</v-tab>
      <v-tab>전체보기</v-tab>
      <v-tab-item
        :key="0"
      >
        <Albums v-if="album === 0" @album-id="moveAlbum"/>
        <Photos v-else :album_id="album"/>
      </v-tab-item>
      <v-tab-item
        :key="1"
      >
        <Photos :album_id="0"/>
      </v-tab-item>
    </v-tabs>
    </v-container>
    </v-sheet>
    </div>
</template>

<script>
  import Albums from '../components/Albums'
  import Photos from '../components/Photos'
  import { mapGetters } from 'vuex'
  export default {
    name: 'MainPage',
    
    components: {
      Albums,
      Photos
    },

    data: () => ({
      account: "",
      username: "",
      album: 0
    }),

    computed: {
    ...mapGetters([
      'options',
      'user',
    ]),
    },

    mounted() {
      this.account = this.user.account
      this.username = this.user.username
    },

    methods: {
      moveAlbum(album_id) {
        this.album = album_id
      },
      setAlbum() {
        this.album = 0
      },
      logout() {
        this.$session.destroy()
        this.$store.dispatch('logout')
        this.$router.replace('/login')
      }
    }
  }
</script>

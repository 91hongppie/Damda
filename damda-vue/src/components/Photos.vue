<template>
    <v-container fluid>
        <v-row>
        <v-col
            v-for="(photo, i) in photos"
            :key="'photo' + photo.id"
            cols="6"
            md="3"
            sm="4"
        >
            <v-img
            @click="movePhoto(i)"
            :src="baseURL + photo.pic_name"
            aspect-ratio="1"
            ></v-img>
        </v-col>
        </v-row>
        <infinite-loading @infinite="infiniteHandler" spinner="spiral">
            <div slot="no-more">더 이상 사진이 없습니다 :)</div>
            <div slot="no-results">등록된 사진이 없습니다 :(</div>
            <div slot="error" slot-scope="{ trigger }">
                에러가 발생했습니다 :( <a href="javascript:;" @click="trigger">다시 시도하기</a>
            </div>
        </infinite-loading>
        <v-dialog v-model="dialog"
                    fullscreen
                    hide-overlay
                    transition="dialog-bottom-transition"
                    scrollable>
            <Photo :style="{'background-color': 'rgba(0,0,0,0.5)'}" :photo_list="photo_list" :index="index" @dialog="DialogEmit"/>
        </v-dialog>
    </v-container>
</template>

<script>
  import http from '../http-common'
  import { mapGetters } from 'vuex'
  import InfiniteLoading from 'vue-infinite-loading'
  import Photo from './Photo'

  export default {
    name: 'Albums',

    props : {
      album_id: {
          type: Number,
          required: true
      },
    },

    components: {
        InfiniteLoading,
        Photo
    },

    data: () => ({
      baseURL : "",
      photos: [],
      limit: 1,
      requestURL: "",
      dialog: false,
      photo_list: [],
      index: 0,
    }),

    computed: {
    ...mapGetters([
      'options',
      'user',
      'family',
    ]),
    },
    mounted() {
        this.baseURL = this.$store.state.server + 'api/'
        if (!this.user) {
        this.$router.replace('/login')
        }
        else {
            if (this.album_id === 0) {
                    this.requestURL = `/api/albums/photo/${this.family}/`
            } else {
                this.requestURL = `/api/albums/photo/${this.family}/${this.album_id}/`
            }
            http.get(`${this.requestURL}?page=${this.limit}`, this.options)
            .then(response => {
                this.photo_list = response.data
            })
            .catch(error => {
                console.log(error)
            })
        }
        },

    methods: {
      infiniteHandler($state) {
          if (!this.family) {
            this.$router.replace('/login')
            }
            else {
                if (this.album_id === 0) {
                    this.requestURL = `/api/albums/photo/${this.family}/web/`
                } else {
                    this.requestURL = `/api/albums/photo/${this.family}/${this.album_id}/web/`
                }
                http.get(`${this.requestURL}?page=${this.limit}`, this.options)
                .then(response => {
                    setTimeout(() => {
                        if (response.data.results.length) {
                        this.photos = this.photos.concat(response.data.results)
                        $state.loaded()
                        this.limit += 1
                        if (response.data.next === null) {
                            $state.complete()
                        }} else {
                            $state.complete()
                        }
                    }, 500)
                })
                .catch(error => {
                    console.log(error)
                })
                }
        },
        movePhoto(i) {
            this.index = i
            this.dialog = true
        },
        DialogEmit() {
            this.dialog = false
            this.index = -1
        }
    }
  }
</script>
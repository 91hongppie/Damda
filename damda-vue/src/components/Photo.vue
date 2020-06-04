<template>
    <div>
        <div>
        <v-btn
            class="ma-3"
            icon
            dark
            @click="closeDialog"
        >
            <v-icon>mdi-close</v-icon>
        </v-btn>
        </div>
        <v-carousel hide-delimiters height="89%" class="mt-3" v-model="model">
            <v-card
            light
            height="100%"
            :style="{'margin-left': '10vw', 'margin-right': '10vw'}"
            class="pb-4"
            >
            <v-list-item>
            <v-list-item-content>
                <v-list-item-title class="headline">{{ photo_list[model].title }}</v-list-item-title>
            </v-list-item-content>
            </v-list-item>
            <v-carousel-item
              v-for="photo in photo_list"
              :key="photo.id"
              :src="baseURL + photo.pic_name"
              contain
              reverse-transition="fade-transition"
              transition="fade-transition"
            ></v-carousel-item>
            </v-card>
        </v-carousel>
    </div>
</template>

<script>
  import { mapGetters } from 'vuex'

  export default {
    name: 'Photo',

    props : {
      photo_list: {
          type: Array,
          required: true
      },
      index: {
          type: Number,
          required: true
      }
    },

    data: () => ({
      baseURL : "",
      model: 0
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
      this.model = this.index
    },
    
    methods: {
      closeDialog() {
        this.$emit('dialog', false)
      }
    },
   
    watch: { index() { 
        this.model = this.index }, 
        }
  }
</script>

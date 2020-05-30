<template>
    <v-container fluid>
      <v-row dense>
        <v-col
          v-for="card in cards"
          :key="card.title"
          :cols="card.flex"
        >
          <v-card>
            <v-img
              :src="card.src"
              class="white--text align-end"
              gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
              height="200px"
            >
              <v-card-title v-text="card.title"></v-card-title>
            </v-img>

            <v-card-actions>
              <v-spacer></v-spacer>

              <v-btn icon>
                <v-icon>mdi-heart</v-icon>
              </v-btn>

              <v-btn icon>
                <v-icon>mdi-bookmark</v-icon>
              </v-btn>

              <v-btn icon>
                <v-icon>mdi-share-variant</v-icon>
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
</template>

<script>
  import axios from 'axios'
  import { mapGetters } from 'vuex'

  export default {
    name: 'Albums',

    data: () => ({
      cards: [
        { title: 'Pre-fab homes', src: 'https://cdn.vuetifyjs.com/images/cards/house.jpg', flex: 12 },
        { title: 'Favorite road trips', src: 'https://cdn.vuetifyjs.com/images/cards/road.jpg', flex: 6 },
        { title: 'Best airlines', src: 'https://cdn.vuetifyjs.com/images/cards/plane.jpg', flex: 6 },
      ],
    }),

    computed: {
    ...mapGetters([
      'options',
      'user',
      'userInfo',
    ]),
    },

    mounted() {
    // if (!this.user) {
    //   this.$router.replace('/signin')
    // }
    // else {
      this.baseURL = "http://localhost:8000/",
      axios.get(`/api/albums/${this.childInfo.id}/`, this.options)
        .then(response => {
          for (var element of response.data.card_set.reverse()) {
            this.all_cards.push({'id': element.id, 'class_name': element.class_name, 
            'card_path': this.baseURL + element.card_path, 'modal': false})
          }
        })
        .catch(error => {
          console.log(error)
        })
    // }
  },

  }
</script>
<template>
  <div class="grey lighten-5" :style="{'height': '100%'}">
    <v-container fluid>
      <v-row>
      <v-col cols="12">
        <v-row
          align="center"
          justify="center"
        >
          <v-card
            class="my-3 pa-6 d-none d-md-inline"
            outlined
            tile
            :style="{'background-color': 'rgba(255,255,255,0)', 'border': 'none'}"
          >
            <v-img :src="require('../assets/logo_splash.png')" class="mb-12" width="450"/>
          </v-card>
          <Login/>
        </v-row>
      </v-col>
    </v-row>
    </v-container>
    <v-footer fixed="true">
    <v-spacer></v-spacer>
    <div><span>&copy; {{ new Date().getFullYear() }} | </span><span>개인정보처리방침</span></div>
  </v-footer>
  </div>
</template>

<script>
  import Login from '../components/Login'

  export default {
    name: 'LoginPage',

    components: {
      Login
    },

    data() {
      return {

      }
    },
  }
</script>
<style>

</style>
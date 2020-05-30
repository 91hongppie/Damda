<template>
  <v-container class="grey lighten-5">
    <v-row no-gutters>
      <v-col
        cols="12"
        sm="6"
      >
        <v-card
          class="pa-2"
          outlined
          tile
        >
          여기는 사진
        </v-card>
      </v-col>
      <v-col
        cols="12"
        sm="6"
      >
      <v-card
          class="pa-2"
          outlined
          tile
        >
          <v-col>
          <v-text-field
            label="Filled"
            placeholder="전화번호, 사용자 이름 또는 이메일"
            filled
            v-model="form.username"
          ></v-text-field>
        </v-col>
        <v-col>
          <v-text-field
            label="Filled"
            placeholder="전화번호, 사용자 이름 또는 이메일"
            filled
            v-model="form.password"
          ></v-text-field>
        </v-col>
        <v-col>
            <v-btn block color="secondary" dark @click="login">로그인??</v-btn>
        </v-col>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>


<script>
import http from '../http-common'
// import router from '../router'
  export default {
    name: 'Login',

    data() {
        return {
            form: {
              username: '',
              password: '',
          },
        }
    },
    methods: {
        login(){
            http.post('/api/api-token-auth/', this.form)
          .then(response => {
            console.log(response.data)
            const token = response.data.token
            this.$session.start()
            this.$session.set('jwt', token)
            this.$store.dispatch('login', response.data)
          })
          .catch((error) => {
            console.log(error)
          })
        }
    }
  }
</script>

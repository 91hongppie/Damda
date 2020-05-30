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
            v-model="credentials.username"
          ></v-text-field>
        </v-col>
        <v-col>
          <v-text-field
            label="Filled"
            placeholder="전화번호, 사용자 이름 또는 이메일"
            filled
            v-model="credentials.password"
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
            credentials : {
                username : "",
                password : ""
            }
        }
    },
    methods: {
        login(){
            // const formData = new FormData()
            // formData.append('username', this.username)
            // formData.append('password',this.password)
            http.get('http://localhost:8000/api/accounts/signup/?username=sjy@gmail.com')
            .then(response =>{
                response.header("Access-Control-Allow-Origin", "*")
                console.log(response)
                // const token = response.data.token
                // this.$session.start()
                // this.$session.set('jwt',token)
                // this.$store.dispatch('login',token)
                // router.push('/')
            })
            .catch(error =>{
                console.log(error)
            })
            this.credentials = []
        }
    }
  }
</script>

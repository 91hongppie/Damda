<template>
  <v-container class="grey lighten-5">
    <v-row no-gutters>
      <v-col cols="12" sm="6">
        <v-card class="pa-2" outlined tile>
          여기는 사진
        </v-card>
      </v-col>
      <v-col cols="12" sm="6">
        <v-card class="pa-2" outlined tile>
          <v-col margin="auto" align="center">
            <v-img src="../assets/logo_main.png" max-width="200px" max-height="200px"></v-img>
          </v-col>
          <v-col>
            <v-text-field v-model="form.username" label="Email" :rules="emailRules" outlined height="5px"></v-text-field>
          </v-col>
          <v-col>
            <v-text-field v-model="form.password" label="Password" :rules="[passwordRules.required, passwordRules.min,]" outlined
              :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'" @click:append="show = !show"
              :type="show ? 'text' : 'password'"></v-text-field>
          </v-col>
          <v-col>
            <v-btn :disabled="!valid" block color="secondary" depressed @click="login">로그인</v-btn>
          </v-col>
          <div :style="{marginLeft:'2%'}">
            <div
              :style="{'background-color':'#000000', height:'1px', width:'33%', display:'inline-block', marginBottom:'5px',marginRight:'5%',marginLeft:'5%'}">
            </div>
            또는
            <div
              :style="{'background-color':'#000000', height:'1px', width:'33%', display:'inline-block', marginBottom:'5px',marginLeft:'5%',marginRight:'5%'}">
            </div>
          </div>
          <v-col>
            <v-img :style="{marginTop:'10px'}" src="../assets/kakao_login_large_wide.png" max-height="35px"></v-img>
          </v-col>
          <v-col align="center">
            <a>비밀번호를 잊으셨나요?</a>
          </v-col>
        </v-card>
        <v-card>
          <v-col :style="{marginTop:'10px'}">
            <p :style="{marginTop:'18px'}" align="center">계정이 없으신가요?
              <a>가입하기</a></p>
          </v-col>
        </v-card>
        <v-col>
          <p align="center" :style="{marginTop:'18px'}">앱을 다운로드하세요</p>
          <v-row no-gutters>

            <v-col :style="{display:'inline-block'}" cols="12" sm="6" align="center">
              <v-img src="../assets/google_play_badge.png" width="100%" height="60px">
              </v-img>
            </v-col>
            <v-col :style="{display:'inline-block'}" cols="12" sm="6" align="center">
              <v-img src="../assets/app_store_badge.png" width="100%" height="60px">
              </v-img>
            </v-col>
          </v-row>
        </v-col>
      </v-col>

    </v-row>

  </v-container>

</template>

<script src="https://developers.kakao.com/sdk/js/kakao.min.js"></script>
<script>
  import http from '../http-common'
  import router from '../router'

  export default {
    name: 'Login',

    data() {
      return {
        justify: [
          'center',
        ],
        show: false,
        valid: {
          email: false,
          password: false,
        },
        email: '',
        emailRules: [
          v => !!v || 'E-mail is required',
          v => /.+@.+\..+/.test(v) || 'E-mail must be valid',
        ],
        passwordRules: {
          required: value => !!value || 'Required.',
          min: v => v.length >= 8 || 'Min 8 characters',
          emailMatch: () => ('The email and password you entered don\'t match'),
        },
        select: null,
        checkbox: false,
        form: {
          username: '',
          password: '',
        },
      }
    },
    methods: {
      validate() {
        this.$refs.form.validate()
      },
      reset() {
        this.$refs.form.reset()
      },
      resetValidation() {
        this.$refs.form.resetValidation()
      },
      login() {
        http.post('/api/api-token-auth/', this.form)
          .then(response => {
            this.$session.start()
            this.$session.set('jwt', response.data)
            this.$store.dispatch('login', response.data)
            router.push('/mainpage')
          })
          .catch((error) => {
            console.log(error)
          })
      }
    },
    computed: {
      isDisabled() {
        return valid == true
      },
  }
  }
</script>
<style>

</style>
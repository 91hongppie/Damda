<template>
  <v-card
    class="my-3 pa-6"
    outlined
    tile
    :style="{'background-color': 'rgba(255,255,255,0)', 'border': 'none', }"
  >
    <v-card class="px-5 pt-6 pb-8" outlined tile min-width="365.6" width="365.6">
  <v-col margin="auto" align="center">
    <v-img src="../assets/logo_main.png" max-width="200px" max-height="200px"></v-img>
  </v-col>
  <v-col class="pb-0">
    <v-text-field dense v-model="form.username" label="이메일" :rules="emailRules" outlined height="5px"></v-text-field>
  </v-col>
  <v-col class="py-0">
    <v-text-field dense v-model="form.password" label="비밀번호" :rules="[passwordRules.required, passwordRules.min,]" outlined
      :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'" @click:append="show = !show"
      :type="show ? 'text' : 'password'"></v-text-field>
  </v-col>
  <v-col class="pt-0">
    <v-btn :disabled="!valid" block color="secondary" depressed @click="login">로그인</v-btn>
  </v-col>
  <div class="my-2">
    <div
      :style="{'background-color':'#000000', height:'1px', width:'33.5%', display:'inline-block', marginBottom:'5px',marginRight:'5%',marginLeft:'5%'}">
    </div>
    또는
    <div
      :style="{'background-color':'#000000', height:'1px', width:'33.5%', display:'inline-block', marginBottom:'5px',marginLeft:'5%'}">
    </div>
  </div>
  <div>
    <v-col>
    <KakaoLogin
    api-key="d43b2976f5db5f67d5dd053188612a95"
    image="kakao_account_login_btn_medium_wide_ov"
    :on-success=onSuccess
    :on-failure=onFailure
    /></v-col>
  </div>
</v-card>
<v-row :style="{'width': '365.6px', 'margin-left':'1px'}">
<v-col cols=6>
  <a :href=baseURL @click.prevent="downloadItem" class="d-block" align="center" :style="{marginTop:'18px'}">앱을 다운로드하세요.</a>
</v-col>
<v-col cols=6>
  <a href='https://play.google.com/store/apps/details?id=com.ebgbs.damda&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img width="100%" alt='다운로드하기 Google Play' src='https://play.google.com/intl/ko/badges/static/images/badges/ko_badge_web_generic.png'/></a>
</v-col>
</v-row>
  </v-card>
</template>

<script>
  import http from '../http-common'
  import router from '../router'
  import KakaoLogin from 'vue-kakao-login'
  import { mapGetters } from 'vuex'

  export default {
    name: 'Login',

    components: {
      KakaoLogin
    },

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
          v => !!v || '이메일을 입력해주세요.',
          v => /.+@.+\..+/.test(v) || '이메일 형식이 맞지 않습니다.',
        ],
        passwordRules: {
          required: value => !!value || '비밀번호를 입력해주세요.',
          min: v => v.length >= 8 || '8자 이상 입력해주세요.'},
        select: null,
        checkbox: false,
        form: {
          username: '',
          password: '',
        },
        baseURL :"",
      }
    },
    mounted() {
      this.baseURL = this.$store.state.server + '/api/uploads/app-release.apk'
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
            this.$session.set('jwt', response.data.token)
            this.$session.set('user', response.data)
            this.$store.dispatch('login', response.data)
            if (!response.data.family) {
                alert("어플에서 가족을 생성한 후 이용해주세요.")
              } else {
              router.push('/mainpage')}
          })
          .catch((error) => {
            alert("아이디와 비밀번호를 확인해 주세요.")
            console.log(error)
          })
      },
      onSuccess(data) {
        const body = {
          access_token: data.access_token
        }
        http.post('/api/accounts/rest-auth/kakao/', body)
          .then(response => {
            this.$session.start()
            this.$session.set('jwt', response.data.token)
            this.$store.dispatch('kakaoLogin', response.data.token)
            http.get('/api/accounts/user/', this.options)
            .then(response => {
              this.$session.set('user', response.data)
              this.$store.dispatch('login', response.data)
              if (!response.data.family) {
                alert("어플에서 가족을 생성한 후 이용해주세요.")
              } else {
              router.push('/mainpage')}
            })
            .catch((error) => {
              console.log(error)
            })
          })
          .catch((error) => {
            console.log(error)
          })
      },
      onFailure(data) {
        console.log(data)
        console.log("failure")
      }, 
    downloadItem () {
    http.get(this.baseURL, { responseType: 'blob' })
      .then(response => {
        const blob = new Blob([response.data], { type: 'application/vnd.android.package-archive' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.click()
        URL.revokeObjectURL(link.href)
      }).catch(console.error)
    }
    },
    computed: {
      ...mapGetters([
      'options',
      'user'
    ]),
  }
  }
</script>
<style>

</style>
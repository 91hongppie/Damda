<template>
  <v-app>
    <router-view :key="$route.fullPath"/>
  </v-app>
</template>

<script>
export default {
  name: 'App',

  components: {
  },

  data() {
    return {
    }
  },
  created() {
    this.$session.start()
      if (this.$session.has('jwt')) {
        this.$store.dispatch('kakaoLogin', this.$session.get('jwt'))
        if (this.$session.has('user')) {
          this.$store.dispatch('login', this.$session.get('user'))
        }
      }

  }
};
</script>

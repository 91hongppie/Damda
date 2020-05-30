<template>
  <v-app>
    <MainPage/>
  </v-app>
</template>

<script>
import MainPage from './router/MainPage';

export default {
  name: 'App',

  components: {
    MainPage,
  },

  data: () => ({
    //
  }),
};
</script>

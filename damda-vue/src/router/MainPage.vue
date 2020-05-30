<template>
    <div>
    <v-container class="overflow-hidden">
    <v-app-bar
      absolute
      color="#92B5D9"
      dark
      shrink-on-scroll
      prominent
      src="https://picsum.photos/1920/1080?random"
      fade-img-on-scroll
      scroll-target="#scrolling-techniques-5"
      scroll-threshold="500"
    >
      <template v-slot:img="{ props }">
        <v-img
          v-bind="props"
          gradient="to top right, rgba(55,236,186,.7), rgba(25,32,72,.7)"
        ></v-img>
      </template>

      <v-toolbar-title>
          <v-img
          :src="require('../assets/logo_main.png')"
          contain
          height="35"
          width="100"
        />
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-btn text class="my-1">로그아웃</v-btn>
    </v-app-bar>
    </v-container>
    <v-sheet
      id="scrolling-techniques-5"
      class="overflow-y-auto"
      max-height="600"
      style="margin-top: 100px"
    >
      <v-container>
        <v-banner>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent cursus nec sem id malesuada.
            Curabitur lacinia sem et turpis euismod, eget elementum ex pretium.
        </v-banner>
        <v-tabs
      background-color="white"
      color="#92B5D9"
      grow
    >
      <v-tab>앨범</v-tab>
      <v-tab>전체보기</v-tab>

      <v-tab-item
        v-for="n in 2"
        :key="n"
      >
        <Albums/>
      </v-tab-item>
    </v-tabs>
    </v-container>
    </v-sheet>
    </div>
</template>

<script>
  import Albums from '../components/Albums';
  export default {
    name: 'MainPage',
    
    components: {
    Albums,
    },

    data: () => ({
      ecosystem: [
        {
          text: 'vuetify-loader',
          href: 'https://github.com/vuetifyjs/vuetify-loader',
        },
        {
          text: 'github',
          href: 'https://github.com/vuetifyjs/vuetify',
        },
        {
          text: 'awesome-vuetify',
          href: 'https://github.com/vuetifyjs/awesome-vuetify',
        },
      ],
      importantLinks: [
        {
          text: 'Documentation',
          href: 'https://vuetifyjs.com',
        },
        {
          text: 'Chat',
          href: 'https://community.vuetifyjs.com',
        },
        {
          text: 'Made with Vuetify',
          href: 'https://madewithvuejs.com/vuetify',
        },
        {
          text: 'Twitter',
          href: 'https://twitter.com/vuetifyjs',
        },
        {
          text: 'Articles',
          href: 'https://medium.com/vuetify',
        },
      ],
      whatsNext: [
        {
          text: 'Explore components',
          href: 'https://vuetifyjs.com/components/api-explorer',
        },
        {
          text: 'Select a layout',
          href: 'https://vuetifyjs.com/layout/pre-defined',
        },
        {
          text: 'Frequently Asked Questions',
          href: 'https://vuetifyjs.com/getting-started/frequently-asked-questions',
        },
      ],
    }),
  }
</script>

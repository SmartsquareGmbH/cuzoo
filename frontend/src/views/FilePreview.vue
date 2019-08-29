<template>
  <v-layout row wrap class="text-xs-center">
    <v-flex xs3>
      <v-btn flat small @click="goPageBack()">
        <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
        Zurück
      </v-btn>
    </v-flex>
    <v-flex v-if="isPDF" xs6>
      <pdf v-for="page in pageCount" :key="page" :src="getPDFSource()" :page="page" class="mb-3" />
    </v-flex>
    <v-flex v-else xs5>
      <v-img :src="url" />
    </v-flex>
    <v-flex xs3></v-flex>
  </v-layout>
</template>

<script>
import { mapGetters } from "vuex"
import pdf from "vue-pdf"

export default {
  components: {
    pdf,
  },
  data() {
    return {
      contactPointId: this.$route.params.contactPointId,
      fileName: this.$route.params.fileName,
      pageCount: 0,
    }
  },
  computed: {
    ...mapGetters(["username", "password"]),
    url() {
      return (
        `${process.env.VUE_APP_API_SCHEME}://${process.env.VUE_APP_API_HOSTNAME}:` +
        `${process.env.VUE_APP_API_PORT}/api/file/download/${this.contactPointId}/${this.fileName}`
      )
    },
    isPDF() {
      return !!this.fileName.inludes("pdf")
    },
  },
  mounted() {
    pdf.createLoadingTask(this.getPDFSource()).then((it) => (this.pageCount = it.numPages))
  },
  methods: {
    getPDFSource() {
      return {
        url: this.url,
        httpHeaders: { Authorization: "Basic " + btoa(this.username + ":" + this.password) },
      }
    },
    goPageBack() {
      this.$router.go(-1)
    },
  },
}
</script>

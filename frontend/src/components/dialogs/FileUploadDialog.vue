<template>
  <v-dialog
    class="text-xs-left"
    :value="value"
    :company-name="companyName"
    :contact-point-id="contactPointId"
    persistent
    max-width="950"
    @input="$emit('input')"
  >
    <v-card>
      <v-card-title class="headline primary" primary-title>
        <v-icon class="mt-0" size="28px" absolute>attach_file</v-icon>
        {{ formTitle }}
      </v-card-title>
      <v-card-text>
        <vue-clip :options="options">
          <template slot="clip-uploader-action">
            <v-card class="bordered pa-1" color="transparent elevation-12 mb-4">
              <div id="drop-area" class="dz-message clickable text-xs-center">
                <h2 class="mt-4 mb-1">
                  Drag & Drop
                </h2>
                <p>Oder klicke zum Hochladen</p>
              </div>
            </v-card>
          </template>
          <template slot="clip-uploader-body" scope="props">
            <div v-for="file in props.files" :key="file.name" class="text-xs-left">
              <p class="title text-xs-left font-weight-light">
                {{ file.name }}
                <v-icon v-if="file.status === 'success'" size="30px" color="success">
                  done_outline
                </v-icon>
                <v-progress-circular
                  v-if="file.status === 'added'"
                  color="primary"
                  indeterminate
                  :width="2"
                  :size="28"
                />
                <v-icon v-if="file.status === 'error'" size="30px" color="error">
                  error_outline
                </v-icon>
              </p>
            </div>
          </template>
        </vue-clip>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" flat @click.native="closeDialog()">OKAY</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from "vuex"

export default {
  props: ["value", "companyName", "contactPointId"],
  data() {
    return {
      formTitle: "Dateien hochladen",
      uploaded: false,
    }
  },
  computed: {
    ...mapGetters(["username", "password"]),
    options() {
      return {
        url: `${process.env.VUE_APP_API_SCHEME}://${process.env.VUE_APP_API_HOSTNAME}:${
          process.env.VUE_APP_API_PORT
        }/api/file/upload/${this.contactPointId}`,
        headers: {
          Authorization: "Basic " + btoa(this.username + ":" + this.password),
        },
        paramName: "file",
      }
    },
  },
  methods: {
    closeDialog() {
      this.$parent.getFileNames()
      this.$emit("input")
    },
  },
}
</script>
<style>
.clickable {
  cursor: pointer;
}

#drop-area {
  border: 2px dashed #ccc;
  border-radius: 20px;
  font-family: sans-serif;
  padding: 30px;
}

#drop-area:hover {
  border-color: #4fc3f7;
}

#drop-area:hover h2 {
  color: #4fc3f7;
}

#drop-area:hover p {
  font-size: 16px;
  color: #4fc3f7;
}

.bordered {
  border-color: #fff;
  border-radius: 20px;
  border-style: solid;
}
</style>

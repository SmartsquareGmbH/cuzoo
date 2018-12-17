<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap>
            <v-flex xs1>
                <v-btn block color="secondary" @click="goPageBack()">
                    <v-icon large dark>arrow_back</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs11>
                <h1 class="text-xs-left display-2 font-weight-thin">
                    {{ this.contactPoint.contact.company.name }}
                </h1>
            </v-flex>
            <v-flex xs12>
                <v-divider/>
            </v-flex>
            <v-flex xs6>
                <v-layout row wrap>
                    <v-flex xs2>
                        <v-card height="100%" dark color="info">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px"> title </v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card height="100%" dark>
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.title }}
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark color="info" height="100%">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">event</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs4>
                        <v-card height="100%">
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.date }}
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark color="info" height="100%">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">{{ getPointTypeIconOf(this.contactPoint.type) }}</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs4>
                        <v-card height="100%">
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.type }}
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark color="green" height="100%" class="centered">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">person</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card dark>
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.contact.name }}
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark color="green">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">comment</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card dark>
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.comment }}
                            </v-card-text>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-flex>
            <v-flex xs6>
                <v-layout row wrap>
                    <v-flex xs2>
                        <v-card dark color="info" height="100%" class="centered">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">attach_file</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark height="100%">
                            <v-card-text class="headline text-xs-center font-weight-light">
                                0
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs6>

                    </v-flex>
                    <v-flex xs12>
                        <vue-clip :options="options">
                            <template slot="clip-uploader-action">
                                <v-card style="border-radius: 20px" class="pa-2">
                                    <div id="drop-area" class="dz-message clickable">
                                        <h2 class="mt-4 mb-4"><v-icon class="mb-1">attach_file</v-icon>Drag and Drop</h2>
                                    </div>
                                </v-card>
                            </template>

                            <template slot="clip-uploader-body" scope="props">
                                <div v-bind:key="file.name" v-for="file in props.files" class="text-xs-left">
                                    <img v-bind:src="file.dataUrl" />
                                    {{ file.name }} {{ file.status }}
                                </div>
                            </template>
                        </vue-clip>
                    </v-flex>
                </v-layout>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
import { mapState } from 'vuex' 
import api from '@/utils/http-common'
import pointStore from '@/stores/points.js'

export default {
    data() {
        return {
            contactPointId: this.$route.params.id,
            options: {
                url: '/upload',
                paramName: 'file'
            }
        }
    },
    computed: {
        contactPoint() {
            return this.contactPoints[this.contactPointId]
        },
        ...mapState(['contactPoints']),
        contactPoints: {
            get() {
                return pointStore.state.contactPoints
            },
            set(contactPoints) {
                pointStore.commit('storeContactPoints', contactPoints)
            }
        }
    },
    methods: {
        getPointTypeIconOf: function (type) {
            switch (type) {
                case 'Telefon': return 'phone'
                case 'E-Mail': return 'mail'
                case 'Social Media': return 'share'
                case 'Persönlich': return 'people'
            }
        },
        goPageBack() {
            this.$router.go(-1)
        }
    }
}
function highlight(e) {
  dropArea.classList.add('highlight')
}

function unhighlight(e) {
  dropArea.classList.remove('highlight')
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
#drop-area.highlight {
  border-color: purple;
}
</style>
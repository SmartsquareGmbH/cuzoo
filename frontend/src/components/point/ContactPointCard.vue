<template>
    <v-layout row wrap class="clickable">
        <v-flex xs1>
            <v-card color="info" height="100%"
                    style="border-radius: 15px; background: linear-gradient(45deg, #4FC3F7, #546E7A)">
                <v-card-text class="pt-4">
                    <v-icon large class="pt-4">{{ getPointTypeIconOf(contactPoint.type) }}</v-icon>
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs11>
            <v-card
                    @click.native="viewContactPoint(contactPoint)"
                    style="border-radius: 15px"
                    height="100%">
                <v-card-title class="secondary headline font-weight-light">
                    {{ contactPoint.title }} •
                    <span class="ml-2 mt-1 primary--text mr-2">{{ contactPoint.date }}</span> •
                    <v-icon class="ml-1">attach_file</v-icon>
                    <span class="ml-1 mt-1 primary--text">{{ fileNames.length }}</span>
                    <v-spacer></v-spacer>
                    <v-chip
                            class="mb-2"
                            color="white">
                        <v-avatar class="info white--text" size="35px">
                            <v-icon>person</v-icon>
                        </v-avatar>
                        <span class="headline secondary--text">
                            {{ contactPoint.contact.name }}
                        </span>
                    </v-chip>
                </v-card-title>
                <v-card-text class="subheading text-xs-left text-truncate" width="20%">
                    {{ contactPoint.comment }}
                </v-card-text>
            </v-card>
        </v-flex>
    </v-layout>
</template>

<script>
    import api from '@/utils/http-common'

    export default {
        props: ['contactPoint'],
        data() {
            return {
                contactPoints: this.$parent.contactPoints,
                company: this.$parent.company,
                fileNames: []
            }
        },
        mounted() {
            this.refreshData();
        },
        methods: {
            getPointTypeIconOf: function (type) {
                switch (type) {
                    case 'Telefon':
                        return 'phone'
                    case 'E-Mail':
                        return 'mail'
                    case 'Social Media':
                        return 'share'
                    case 'Persönlich':
                        return 'people'
                }
            },
            viewContactPoint(contactPoint) {
                this.$router.push(this.$route.fullPath + '/' + (contactPoint.id));
            },
            refreshData() {
                api.get(`file/get/names/${this.contactPoint.id}`)
                    .then(response => {
                        if (response.data.length > 0) {
                            this.fileNames = response.data;
                        }
                    })
                    .catch(error => {
                        console.log(error);
                    });
            }
        }
    }
</script>

<style>
    .clickable {
        cursor: pointer;
    }
</style>
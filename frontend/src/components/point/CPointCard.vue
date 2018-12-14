<template>
    <v-layout row wrap>
        <v-flex xs1>
            <v-card color="info" height="100%" style="border-radius: 15px">
                <v-card-text class="pt-4">
                    <v-icon large class="pt-4">{{ getPointTypeIconOf(cPoint.type) }}</v-icon>
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs11>
            <v-card 
            style="border-radius: 15px"
            height="100%">
                <v-card-title class="secondary headline font-weight-light">
                    {{ cPoint.title }} •                                     
                    <span class="ml-2 mt-1 primary--text mr-2">{{ cPoint.date }}</span> •
                    <v-icon class="ml-1">attach_file</v-icon>
                    <span class="ml-1 mt-1 primary--text">0</span>
                    <v-spacer></v-spacer>
                    <v-chip
                    class="mb-2"
                    color="white">
                        <v-avatar class="info white--text" size="35px">
                            <v-icon>person</v-icon>
                        </v-avatar>
                        <span class="headline secondary--text">
                            {{ cPoint.contact.name }}
                        </span>
                    </v-chip>
                </v-card-title>
                <v-card-text class="subheading text-xs-left text-truncate">
                    {{ cPoint.comment }}
                </v-card-text>
            </v-card>
        </v-flex>
    </v-layout>    
</template>

<script>
export default {
    props: ['cPoint'],
    methods: {
        getPointTypeIconOf: function (type) {
            switch(type) {
                case 'Telefon': return 'phone'
                case 'E-Mail': return 'mail'
                case 'Social Media': return 'share'
            }
        },
    }
}
</script>

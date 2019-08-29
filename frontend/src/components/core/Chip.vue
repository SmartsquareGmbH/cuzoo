<template>
  <v-chip :class="`body-2 ${fontColor}--text my-0`" small>
    <slot />
  </v-chip>
</template>

<script>
export default {
  props: ["fontColor"],
}
</script>

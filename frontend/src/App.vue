<script setup>
import { ref } from 'vue';

// 로그인
async function login() {
  const res = await fetch('http://localhost:8080/auth/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({loginId : 'test1', pwd : 'tmpmember'})
  })
  const data = await res.json()
  console.log(data)
  
}

// 게시물 목록 가져옴
const posts = ref([]);
  async function loadPosts() {
    const res = await fetch('http://localhost:8080/posts')
    const data = await res.json()
    posts.value = data.posts
  }
</script>

<template>
  <button @click="login">login</button>


  <button @click="loadPosts">loadPosts</button>
  <ul>
    <li v-for="post in posts">{{ post.title }}</li>
  </ul>
  <h1>게시판</h1>
  <p>
    Visit <a href="https://vuejs.org/" target="_blank" rel="noopener">vuejs.org</a> to read the documentation
  </p>
</template>

<style scoped></style>

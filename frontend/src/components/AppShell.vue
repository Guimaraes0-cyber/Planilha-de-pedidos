<script setup>
import Sidebar from './Sidebar.vue'
</script>

<template>
  <div class="app-header">
    <div class="wordmark-row">
      <div>
        <p class="wordmark">Intimissimi &amp; Calzedonia</p>
        <div class="subtitle">Controle de pedidos &middot; 3 lojas</div>
      </div>
    </div>
    <div class="header-actions">
      <slot name="actions" />
    </div>
  </div>
  <div class="app-shell">
    <Sidebar />
    <div class="main-content">
      <slot />
    </div>
  </div>
</template>

<style scoped>
#app-root, .app-header{max-width:1180px;margin:0 auto;padding:0 20px;}
.app-header{
  display:flex;flex-wrap:wrap;align-items:flex-end;justify-content:space-between;gap:16px;
  padding-top:28px;margin-bottom:22px;
}
.wordmark{font-family:'Source Serif 4',serif;font-weight:700;font-size:26px;margin:0;color:var(--brand-dark);}
.subtitle{color:var(--ink-soft);font-size:11.5px;margin-top:2px;letter-spacing:1px;text-transform:uppercase;}
.header-actions{display:flex;gap:10px;flex-wrap:wrap;}
.app-shell{max-width:1180px;margin:0 auto;padding:0 20px 60px;display:flex;gap:22px;align-items:flex-start;}
.main-content{flex:1;min-width:0;}
@media (max-width:820px){ .app-shell{flex-direction:column;} }
</style>

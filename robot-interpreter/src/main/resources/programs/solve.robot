
hairetsu vis = {3375};

seisu kansu mul15(seisu val) kido
  seisu s = 0;
  shuki k = 0 : 14 kido
    s = s + val;
  shushi;
  return s;
shushi;

seisu kansu mul225(seisu val) kido
  seisu s = 0;
  shuki k = 0 : 14 kido
    s = s + mul15(val);
  shushi;
  return s;
shushi;

seisu kansu cellId(seisu x, seisu y, seisu z) kido
  return x + mul15(y) + mul225(z);
shushi;

seisu kansu wasHere(seisu x, seisu y, seisu z) kido
  return vis[cellId(x, y, z)];
shushi;

seisu kansu markHere(seisu x, seisu y, seisu z) kido
  vis[cellId(x, y, z)] = 1;
  return 0;
shushi;

seisu kansu tryFwd() kido
  seisu d = o_0;
  sorenara d > 0 kido
    o_o;
    seisu r = solve();
    sorenara r > 0 kido
      return 1;
    shushi;
    ~_~;
  shushi;
  return 0;
shushi;

seisu kansu tryBack() kido
  seisu d = ~_0;
  sorenara d > 0 kido
    ~_~;
    seisu r = solve();
    sorenara r > 0 kido
      return 1;
    shushi;
    o_o;
  shushi;
  return 0;
shushi;

seisu kansu tryRight() kido
  seisu d = >_0;
  sorenara d > 0 kido
    >_>;
    seisu r = solve();
    sorenara r > 0 kido
      return 1;
    shushi;
    <_<;
  shushi;
  return 0;
shushi;

seisu kansu tryLeft() kido
  seisu d = <_0;
  sorenara d > 0 kido
    <_<;
    seisu r = solve();
    sorenara r > 0 kido
      return 1;
    shushi;
    >_>;
  shushi;
  return 0;
shushi;

seisu kansu tryUp() kido
  seisu d = ^_0;
  sorenara d > 0 kido
    ^_^;
    seisu r = solve();
    sorenara r > 0 kido
      return 1;
    shushi;
    v_v;
  shushi;
  return 0;
shushi;

seisu kansu tryDown() kido
  seisu d = v_0;
  sorenara d > 0 kido
    v_v;
    seisu r = solve();
    sorenara r > 0 kido
      return 1;
    shushi;
    ^_^;
  shushi;
  return 0;
shushi;

seisu kansu solve() kido
  rippotai p = *_*;
  sorenara p=>exit kido
    return 1;
  shushi;
  sorenara wasHere(p=>x, p=>y, p=>z) > 0 kido
    return 0;
  shushi;
  markHere(p=>x, p=>y, p=>z);
  sorenara tryFwd() > 0 kido
    return 1;
  shushi;
  sorenara tryBack() > 0 kido
    return 1;
  shushi;
  sorenara tryRight() > 0 kido
    return 1;
  shushi;
  sorenara tryLeft() > 0 kido
    return 1;
  shushi;
  sorenara tryUp() > 0 kido
    return 1;
  shushi;
  sorenara tryDown() > 0 kido
    return 1;
  shushi;
  return 0;
shushi;

seisu kansu main() kido
  seisu result = solve();
  return result;
shushi;

main();

function aqb(){}
function s5b(){}
function L4b(){M4b.call(this,false)}
function n5b(a,b){p5b.call(this,a,false);this.c=b}
function o5b(a,b){p5b.call(this,a,false);l5b(this,b)}
function m5b(a){p5b.call(this,'GWT',true);l5b(this,a)}
function bqb(a){this.d=a;this.c=lab(this.d.b)}
function p4b(a,b){return w4b(a,b,a.b.c)}
function w4b(a,b,c){if(c<0||c>a.b.c){throw new Jmc}a.p&&(b.R[QFc]=2,undefined);o4b(a,c,b.R);Ntc(a.b,c,b);return b}
function l5b(a,b){a.e=b;!!a.d&&K4b(a.d,a);if(b){b.R.tabIndex=-1;a.R.setAttribute(SGc,oCc)}else{a.R.setAttribute(SGc,JDc)}}
function t5b(){var a;ke(this,$doc.createElement(zDc));this.R[Nzc]='gwt-MenuItemSeparator';a=$doc.createElement(Szc);kl(this.R,H7b(a));a[Nzc]='menuSeparatorInner'}
function gab(a){var b,c;b=eP(a.b.Vd(JGc),138);if(b==null){c=WO(P4,{124:1,135:1,138:1},1,[KGc,'R\xE9tablir','Couper','Copier','Coller']);a.b.Xd(JGc,c);return c}else{return b}}
function hab(a){var b,c;b=eP(a.b.Vd(LGc),138);if(b==null){c=WO(P4,{124:1,135:1,138:1},1,['Nouveau','Ouvrir',MGc,'R\xE9cent','Quitter']);a.b.Xd(LGc,c);return c}else{return b}}
function kab(a){var b,c;b=eP(a.b.Vd(PGc),138);if(b==null){c=WO(P4,{124:1,135:1,138:1},1,['Contenu','Biscuit de fortune','\xC0 propos de GWT']);a.b.Xd(PGc,c);return c}else{return b}}
function jab(a){var b,c;b=eP(a.b.Vd(OGc),138);if(b==null){c=WO(P4,{124:1,135:1,138:1},1,['T\xE9l\xE9charger','Exemples',aCc,'GWiTtez avec le programme']);a.b.Xd(OGc,c);return c}else{return b}}
function iab(a){var b,c;b=eP(a.b.Vd(NGc),138);if(b==null){c=WO(P4,{124:1,135:1,138:1},1,['P\xEAcher dans le d\xE9sert.txt','Comment apprivoiser un perroquet sauvage',"L'\xE9levage des \xE9meus pour les nuls"]);a.b.Xd(NGc,c);return c}else{return b}}
function lab(a){var b,c;b=eP(a.b.Vd(QGc),138);if(b==null){c=WO(P4,{124:1,135:1,138:1},1,["Merci d'avoir s\xE9lectionn\xE9 une option de menu",'Une s\xE9lection vraiment pertinente',"N'avez-vous rien de mieux \xE0 faire que de s\xE9lectionner des options de menu?","Essayez quelque chose d'autre","ceci n'est qu'un menu!",'Un autre clic gaspill\xE9']);a.b.Xd(QGc,c);return c}else{return b}}
function Ypb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new bqb(a);n=new L4b;n.c=true;n.R.style[Ozc]='500px';n.f=true;q=new M4b(true);p=iab(a.b);for(k=0;k<p.length;++k){n4b(q,new n5b(p[k],o))}d=new M4b(true);d.f=true;n4b(n,new o5b('Fichier',d));e=hab(a.b);for(k=0;k<e.length;++k){if(k==3){p4b(d,new t5b);n4b(d,new o5b(e[3],q));p4b(d,new t5b)}else{n4b(d,new n5b(e[k],o))}}b=new M4b(true);n4b(n,new o5b('\xC9dition',b));c=gab(a.b);for(k=0;k<c.length;++k){n4b(b,new n5b(c[k],o))}f=new M4b(true);n4b(n,new m5b(f));g=jab(a.b);for(k=0;k<g.length;++k){n4b(f,new n5b(g[k],o))}i=new M4b(true);p4b(n,new t5b);n4b(n,new o5b('Aide',i));j=kab(a.b);for(k=0;k<j.length;++k){n4b(i,new n5b(j[k],o))}Xec(n.R,Lzc,RGc);J4b(n,RGc);return n}
var SGc='aria-haspopup',RGc='cwMenuBar',JGc='cwMenuBarEditOptions',LGc='cwMenuBarFileOptions',NGc='cwMenuBarFileRecents',OGc='cwMenuBarGWTOptions',PGc='cwMenuBarHelpOptions',QGc='cwMenuBarPrompts';_=bqb.prototype=aqb.prototype=new Y;_.dc=function cqb(){rSb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function dqb(){return xW};_.cM={82:1};_.b=0;_.d=null;_=eqb.prototype;_.bc=function iqb(){J8(this.c,Ypb(this.b))};_=L4b.prototype=m4b.prototype;_=o5b.prototype=n5b.prototype=m5b.prototype=h5b.prototype;_=t5b.prototype=s5b.prototype=new fe;_.gC=function u5b(){return b0};_.cM={91:1,97:1,110:1};var xW=qnc(HEc,'CwMenuBar$1'),b0=qnc(jEc,'MenuItemSeparator');Jzc(tj)(18);
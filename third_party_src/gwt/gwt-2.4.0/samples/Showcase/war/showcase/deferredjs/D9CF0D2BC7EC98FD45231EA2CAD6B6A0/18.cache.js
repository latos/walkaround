function Gqb(){}
function _5b(){}
function s5b(){t5b.call(this,false)}
function W5b(a,b){Y5b.call(this,a,false);this.c=b}
function X5b(a,b){Y5b.call(this,a,false);U5b(this,b)}
function V5b(a){Y5b.call(this,'GWT',true);U5b(this,a)}
function Hqb(a){this.d=a;this.c=Rab(this.d.b)}
function Y4b(a,b){return d5b(a,b,a.b.c)}
function d5b(a,b,c){if(c<0||c>a.b.c){throw new Cnc}a.p&&(b.R[OGc]=2,undefined);X4b(a,c,b.R);Guc(a.b,c,b);return b}
function U5b(a,b){a.e=b;!!a.d&&r5b(a.d,a);if(b){b.R.tabIndex=-1;a.R.setAttribute(QHc,gDc)}else{a.R.setAttribute(QHc,HEc)}}
function a6b(){var a;xe(this,$doc.createElement(xEc));this.R[GAc]='gwt-MenuItemSeparator';a=$doc.createElement(LAc);yl(this.R,o8b(a));a[GAc]='menuSeparatorInner'}
function Mab(a){var b,c;b=HP(a.b.Zd(HHc),138);if(b==null){c=xP(t5,{124:1,135:1,138:1},1,[IHc,'R\xE9tablir','Couper','Copier','Coller']);a.b._d(HHc,c);return c}else{return b}}
function Nab(a){var b,c;b=HP(a.b.Zd(JHc),138);if(b==null){c=xP(t5,{124:1,135:1,138:1},1,['Nouveau','Ouvrir',KHc,'R\xE9cent','Quitter']);a.b._d(JHc,c);return c}else{return b}}
function Qab(a){var b,c;b=HP(a.b.Zd(NHc),138);if(b==null){c=xP(t5,{124:1,135:1,138:1},1,['Contenu','Biscuit de fortune','\xC0 propos de GWT']);a.b._d(NHc,c);return c}else{return b}}
function Pab(a){var b,c;b=HP(a.b.Zd(MHc),138);if(b==null){c=xP(t5,{124:1,135:1,138:1},1,['T\xE9l\xE9charger','Exemples',UCc,'GWiTtez avec le programme']);a.b._d(MHc,c);return c}else{return b}}
function Oab(a){var b,c;b=HP(a.b.Zd(LHc),138);if(b==null){c=xP(t5,{124:1,135:1,138:1},1,['P\xEAcher dans le d\xE9sert.txt','Comment apprivoiser un perroquet sauvage',"L'\xE9levage des \xE9meus pour les nuls"]);a.b._d(LHc,c);return c}else{return b}}
function Rab(a){var b,c;b=HP(a.b.Zd(OHc),138);if(b==null){c=xP(t5,{124:1,135:1,138:1},1,["Merci d'avoir s\xE9lectionn\xE9 une option de menu",'Une s\xE9lection vraiment pertinente',"N'avez-vous rien de mieux \xE0 faire que de s\xE9lectionner des options de menu?","Essayez quelque chose d'autre","ceci n'est qu'un menu!",'Un autre clic gaspill\xE9']);a.b._d(OHc,c);return c}else{return b}}
function Cqb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Hqb(a);n=new s5b;n.c=true;n.R.style[HAc]='500px';n.f=true;q=new t5b(true);p=Oab(a.b);for(k=0;k<p.length;++k){W4b(q,new W5b(p[k],o))}d=new t5b(true);d.f=true;W4b(n,new X5b('Fichier',d));e=Nab(a.b);for(k=0;k<e.length;++k){if(k==3){Y4b(d,new a6b);W4b(d,new X5b(e[3],q));Y4b(d,new a6b)}else{W4b(d,new W5b(e[k],o))}}b=new t5b(true);W4b(n,new X5b('\xC9dition',b));c=Mab(a.b);for(k=0;k<c.length;++k){W4b(b,new W5b(c[k],o))}f=new t5b(true);W4b(n,new V5b(f));g=Pab(a.b);for(k=0;k<g.length;++k){W4b(f,new W5b(g[k],o))}i=new t5b(true);Y4b(n,new a6b);W4b(n,new X5b('Aide',i));j=Qab(a.b);for(k=0;k<j.length;++k){W4b(i,new W5b(j[k],o))}Efc(n.R,EAc,PHc);q5b(n,PHc);return n}
var QHc='aria-haspopup',PHc='cwMenuBar',HHc='cwMenuBarEditOptions',JHc='cwMenuBarFileOptions',LHc='cwMenuBarFileRecents',MHc='cwMenuBarGWTOptions',NHc='cwMenuBarHelpOptions',OHc='cwMenuBarPrompts';_=Hqb.prototype=Gqb.prototype=new Y;_.hc=function Iqb(){$Sb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function Jqb(){return aX};_.cM={82:1};_.b=0;_.d=null;_=Kqb.prototype;_.fc=function Oqb(){n9(this.c,Cqb(this.b))};_=s5b.prototype=V4b.prototype;_=X5b.prototype=W5b.prototype=V5b.prototype=Q5b.prototype;_=a6b.prototype=_5b.prototype=new se;_.gC=function b6b(){return G0};_.cM={91:1,97:1,110:1};var aX=joc(FFc,'CwMenuBar$1'),G0=joc(hFc,'MenuItemSeparator');CAc(Hj)(18);
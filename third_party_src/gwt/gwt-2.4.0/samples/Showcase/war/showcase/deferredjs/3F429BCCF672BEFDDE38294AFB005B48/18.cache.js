function Uqb(){}
function n6b(){}
function G5b(){H5b.call(this,false)}
function i6b(a,b){k6b.call(this,a,false);this.c=b}
function j6b(a,b){k6b.call(this,a,false);g6b(this,b)}
function h6b(a){k6b.call(this,'GWT',true);g6b(this,a)}
function Vqb(a){this.d=a;this.c=dbb(this.d.b)}
function j5b(a,b){return r5b(a,b,a.b.c)}
function r5b(a,b,c){if(c<0||c>a.b.c){throw new Wnc}a.p&&(b.R[dHc]=2,undefined);i5b(a,c,b.R);$uc(a.b,c,b);return b}
function g6b(a,b){a.e=b;!!a.d&&F5b(a.d,a);if(b){(n2b(),b.R).tabIndex=-1;a.R.setAttribute(fIc,FDc)}else{a.R.setAttribute(fIc,ZEc)}}
function o6b(){var a;ye(this,$doc.createElement(PEc));this.R[$Ac]='gwt-MenuItemSeparator';a=$doc.createElement(dBc);Sl(this.R,C8b(a));a[$Ac]='menuSeparatorInner'}
function $ab(a){var b,c;b=QP(a.b.be(YHc),138);if(b==null){c=GP(H5,{124:1,135:1,138:1},1,[ZHc,'R\xE9tablir','Couper','Copier','Coller']);a.b.de(YHc,c);return c}else{return b}}
function _ab(a){var b,c;b=QP(a.b.be($Hc),138);if(b==null){c=GP(H5,{124:1,135:1,138:1},1,['Nouveau','Ouvrir',_Hc,'R\xE9cent','Quitter']);a.b.de($Hc,c);return c}else{return b}}
function cbb(a){var b,c;b=QP(a.b.be(cIc),138);if(b==null){c=GP(H5,{124:1,135:1,138:1},1,['Contenu','Biscuit de fortune','\xC0 propos de GWT']);a.b.de(cIc,c);return c}else{return b}}
function bbb(a){var b,c;b=QP(a.b.be(bIc),138);if(b==null){c=GP(H5,{124:1,135:1,138:1},1,['T\xE9l\xE9charger','Exemples',rDc,'GWiTtez avec le programme']);a.b.de(bIc,c);return c}else{return b}}
function abb(a){var b,c;b=QP(a.b.be(aIc),138);if(b==null){c=GP(H5,{124:1,135:1,138:1},1,['P\xEAcher dans le d\xE9sert.txt','Comment apprivoiser un perroquet sauvage',"L'\xE9levage des \xE9meus pour les nuls"]);a.b.de(aIc,c);return c}else{return b}}
function dbb(a){var b,c;b=QP(a.b.be(dIc),138);if(b==null){c=GP(H5,{124:1,135:1,138:1},1,["Merci d'avoir s\xE9lectionn\xE9 une option de menu",'Une s\xE9lection vraiment pertinente',"N'avez-vous rien de mieux \xE0 faire que de s\xE9lectionner des options de menu?","Essayez quelque chose d'autre","ceci n'est qu'un menu!",'Un autre clic gaspill\xE9']);a.b.de(dIc,c);return c}else{return b}}
function Qqb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Vqb(a);n=new G5b;n.c=true;n.R.style[_Ac]='500px';n.f=true;q=new H5b(true);p=abb(a.b);for(k=0;k<p.length;++k){h5b(q,new i6b(p[k],o))}d=new H5b(true);d.f=true;h5b(n,new j6b('Fichier',d));e=_ab(a.b);for(k=0;k<e.length;++k){if(k==3){j5b(d,new o6b);h5b(d,new j6b(e[3],q));j5b(d,new o6b)}else{h5b(d,new i6b(e[k],o))}}b=new H5b(true);h5b(n,new j6b('\xC9dition',b));c=$ab(a.b);for(k=0;k<c.length;++k){h5b(b,new i6b(c[k],o))}f=new H5b(true);h5b(n,new h6b(f));g=bbb(a.b);for(k=0;k<g.length;++k){h5b(f,new i6b(g[k],o))}i=new H5b(true);j5b(n,new o6b);h5b(n,new j6b('Aide',i));j=cbb(a.b);for(k=0;k<j.length;++k){h5b(i,new i6b(j[k],o))}Sfc(n.R,YAc,eIc);E5b(n,eIc);return n}
var fIc='aria-haspopup',eIc='cwMenuBar',YHc='cwMenuBarEditOptions',$Hc='cwMenuBarFileOptions',aIc='cwMenuBarFileRecents',bIc='cwMenuBarGWTOptions',cIc='cwMenuBarHelpOptions',dIc='cwMenuBarPrompts';_=Vqb.prototype=Uqb.prototype=new Y;_.hc=function Wqb(){kTb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function Xqb(){return mX};_.cM={82:1};_.b=0;_.d=null;_=Yqb.prototype;_.fc=function arb(){B9(this.c,Qqb(this.b))};_=G5b.prototype=g5b.prototype;_=j6b.prototype=i6b.prototype=h6b.prototype=c6b.prototype;_=o6b.prototype=n6b.prototype=new te;_.gC=function p6b(){return S0};_.cM={91:1,97:1,110:1};var mX=Doc(XFc,'CwMenuBar$1'),S0=Doc(zFc,'MenuItemSeparator');WAc(Hj)(18);
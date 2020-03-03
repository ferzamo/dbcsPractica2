import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AnadirReservaComponent } from './anadir-reserva/anadir-reserva.component';
import { LoginUsuarioComponent } from './login-usuario/login-usuario.component';
import { ModificarReservaComponent } from './modificar-reserva/modificar-reserva.component';
import { MostrarReservasComponent } from './mostrar-reservas/mostrar-reservas.component';
import  { ReservaFinalizarComponent } from './reserva-finalizar/reserva-finalizar.component'



const routes: Routes = [
  {path: 'reservas/login', component:LoginUsuarioComponent},
  {path: 'reservas/:id', component:MostrarReservasComponent},
  {path: 'reservas/:id/editar', component:ModificarReservaComponent},
  {path: 'reservas/:id/nueva', component:AnadirReservaComponent},
  {path: 'reservas/:id/nueva/:matricula', component:ReservaFinalizarComponent},
  {path: '**', redirectTo:'reservas/login', pathMatch:'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

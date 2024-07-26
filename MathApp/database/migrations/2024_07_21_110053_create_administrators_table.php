<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateAdministratorsTable extends Migration
{
    public function up()
    {
        Schema::create('administrators', function (Blueprint $table) {
            $table->increments('adminID');
            $table->string('adminName', 20);
            $table->string('password', 50);
            $table->timestamps();
        });
    }

    public function down()
    {
        Schema::dropIfExists('administrators');
    }
}
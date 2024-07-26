<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateSchoolsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('schools', function (Blueprint $table) {
            $table->integer('schoolRegNo')->primary();
            $table->string('schoolName', 30);
            $table->string('district', 15);
            $table->unsignedInteger('schoolRepID'); // Use unsignedInteger for foreign keys
            $table->string('emailAddress', 30)->unique();
            $table->string('password', 60);
            $table->timestamps(); // Adds created_at and updated_at columns

            // Define the foreign key constraint
            $table->foreign('schoolRepID')
                  ->references('id') // Assuming the referenced column is 'id'
                  ->on('representatives'); // Assuming the referenced table is 'representatives'
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('schools');
    }
}
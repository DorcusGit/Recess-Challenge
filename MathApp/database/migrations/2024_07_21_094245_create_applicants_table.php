<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateApplicantsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('applicants', function (Blueprint $table) {
            $table->id('applicantID'); // Auto-incrementing primary key
            $table->integer('schoolRegNo');
            $table->string('emailAddress', 30)->unique();
            $table->string('userName', 20);
            $table->string('imagePath', 10);
            $table->string('firstName', 10);
            $table->string('lastName', 15);
            $table->string('password', 50);
            $table->date('dateOfBirth');
            $table->foreign('schoolRegNo')->references('schoolRegNo')->on('schools'); // Foreign key reference
            $table->timestamps(); // Adds created_at and updated_at columns
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('applicants');
    }
}
